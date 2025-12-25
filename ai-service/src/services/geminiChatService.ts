import { GoogleGenAI } from '@google/genai';
import {ToolDef} from '../mcp/tools';
import {ChatStructuredResponseSchema} from './chatSchemas';

export type ChatOptions = {
  apiKey: string;
  model?: string;
};

export type ChatResult = {
  text: string;
  toolsUsed: { name: string; args: any }[];
  // When we can parse/validate model output as structured JSON, return it here.
  json?: unknown;
};

function extractText(resp: any): string {
  // @google/genai responses can expose text in different shapes; keep this defensive.
  if (!resp) return '';
  if (typeof resp.text === 'string') return resp.text;
  if (typeof resp?.response?.text === 'function') return resp.response.text();
  if (typeof resp?.response?.text === 'string') return resp.response.text;
  // Fallback: stitch together candidate parts if present
  const parts = resp?.candidates?.[0]?.content?.parts;
  if (Array.isArray(parts)) {
    return parts.map((p: any) => p?.text).filter(Boolean).join('\n');
  }
  return '';
}

type ToolCall = { name: string; args: any };

function extractToolCalls(resp: any): ToolCall[] {
  // Common for function calling: candidates[0].content.parts[].functionCall
  const parts = resp?.candidates?.[0]?.content?.parts;
  if (!Array.isArray(parts)) return [];

  const calls: ToolCall[] = [];
  for (const p of parts) {
    const fc = (p as any)?.functionCall;
    if (fc?.name) {
      calls.push({ name: fc.name, args: fc.args ?? {} });
    }
  }
  return calls;
}

function buildToolsPayload(tools: ToolDef[]) {
  // @google/genai uses function declarations.
  // If we provide empty schemas, Gemini is much less likely to call the tools correctly.
  return [
    {
      functionDeclarations: tools.map(t => ({
        name: t.name,
        description: t.description,
        // Prefer tool-provided JSON schema params; fallback to permissive object.
        parameters: (t as any).parametersJsonSchema ?? {
          type: 'object',
          properties: {},
          additionalProperties: true
        }
      }))
    }
  ];
}

function tryParseJson(text: string): any | null {
  const trimmed = (text || '').trim();
  if (!trimmed) return null;

  // If the model wraps JSON in fences, strip them.
  const fenced = trimmed.match(/```(?:json)?\s*([\s\S]*?)\s*```/i);
  const candidate = fenced ? fenced[1].trim() : trimmed;

  try {
    return JSON.parse(candidate);
  } catch {
    return null;
  }
}

type IntentMode = 'ai_only' | 'tool_required';

type IntentDecision = {
  mode: IntentMode;
  reason?: string;
};

function requiresTools(userMessage: string): IntentDecision {
  const msg = (userMessage || '').toLowerCase();

  // AI-only is restricted to meta/help/non-factual questions about the assistant itself.
  // Everything else defaults to tool-required to avoid ungrounded content.
  const aiOnlyPatterns: RegExp[] = [
    /^\s*(hi|hello|hey)\b/,
    /\b(help|how do i|what can you do|capabilities)\b/,
    /\b(explain the app|how travel-mate works|what tools do you have)\b/
  ];
  if (aiOnlyPatterns.some((re) => re.test(msg))) return { mode: 'ai_only' };

  // Tool-required if the user asks for anything that implies Travel-Mate data.
  // This intentionally includes itinerary/budget requests: we won't accept "general knowledge" itineraries.
  const toolRequiredPatterns: Array<{ re: RegExp; reason: string }> = [
    { re: /\b(trending|popular|top)\b/, reason: 'User asked for trending/popular content' },
    { re: /\b(trip|trips|journal|journals)\b/, reason: 'User asked about trips/journals' },
    { re: /\b(create|plan|itinerary|budget|estimated budget)\b/, reason: 'User asked for an itinerary/budget which must be grounded in stored trips/journals' },
    { re: /\b(recommend|suggest|best)\b/, reason: 'User asked for recommendations that must be grounded in DB results' },
    { re: /\b(destination|place|city)\b/, reason: 'User asked destination-specific content' }
  ];

  for (const p of toolRequiredPatterns) {
    if (p.re.test(msg)) return { mode: 'tool_required', reason: p.reason };
  }

  return { mode: 'tool_required', reason: 'Default tool-first policy' };
}

const SYSTEM_INSTRUCTION =
  `You are Travel-Mate's AI assistant.\n\n` +
  `CRITICAL POLICY:\n` +
  `1) Tool-first: If the user asks for trips, journals, recommendations, trending content, itineraries, budgets, or anything destination-specific, you MUST call one or more tools before answering.\n` +
  `2) If you have not called a tool, you MUST return an error JSON stating you need to query Travel-Mate data first.\n` +
  `3) Do NOT use general knowledge to invent itineraries/budgets. Only summarize or derive from tool outputs.\n` +
  `4) Do NOT invent trip/journal IDs or claim DB actions unless a tool returned it.\n\n` +
  `Output MUST be valid JSON only (no markdown, no prose). It must match this exact shape:\n` +
  `{"type":"itinerary"|"answer"|"error","request":{"destination"?:string,"days"?:number,"people"?:number},"itinerary"?:{"destination":string,"days":Array<{"day":number,"items":Array<{"title":string,"description":string,"location"?:string,"estimatedCost"?:{"currency":string,"amount":number}}>}>,"notes"?:string[]},"budget"?:{"currency":string,"total"?:{"currency":string,"amount":number},"perPerson"?:{"currency":string,"amount":number},"breakdown"?:Array<{"label":string,"estimated":{"currency":string,"amount":number},"notes"?:string}>,"assumptions"?:string[]},"error"?:{"code":string,"message":string,"details"?:any},"sources":{"toolsUsed":string[],"note"?:string}}\n` +
  `IMPORTANT: For non-error responses, sources.toolsUsed MUST be a non-empty array.\n`;

function toolPolicyErrorJson(reason: string, toolsUsed: { name: string; args: any }[]) {
  return {
    type: 'error',
    request: {},
    error: {
      code: 'TOOLS_REQUIRED',
      message: 'This request must be answered using Travel-Mate tools, but no tools were called.',
      details: { reason }
    },
    sources: { toolsUsed: toolsUsed.map((t) => t.name), note: 'Tool-first policy enforced' }
  };
}

async function validateOrRepairJson(
  ai: GoogleGenAI,
  model: string,
  toolsPayload: any,
  contents: any[],
  latestText: string
): Promise<{ json: any | null; text: string }> {
  const parsed = tryParseJson(latestText);
  if (parsed) {
    const validated = ChatStructuredResponseSchema.safeParse(parsed);
    if (validated.success) return { json: validated.data, text: latestText };
  }

  // One repair attempt: ask model to output ONLY valid JSON for the schema.
  const repairContents = [
    ...contents,
    {
      role: 'user',
      parts: [
        {
          text:
            `Your previous response was not valid JSON for the required schema. ` +
            `Re-output ONLY valid JSON that matches the schema exactly. ` +
            `Do not include markdown or extra text. Here is your previous output:\n${latestText}`
        }
      ]
    }
  ];

  const repairResp = await ai.models.generateContent({
    model,
    contents: repairContents,
    config: {
      tools: toolsPayload
    }
  } as any);

  const repairedText = extractText(repairResp);
  const repairedJson = tryParseJson(repairedText);
  if (repairedJson) {
    const validated = ChatStructuredResponseSchema.safeParse(repairedJson);
    if (validated.success) return { json: validated.data, text: repairedText };
  }

  return { json: null, text: latestText };
}

function buildGenerateConfig(toolsPayload: any, intentMode: IntentMode) {
  // NOTE: Gemini currently rejects "function calling" + responseMimeType application/json.
  // So, when tools are enabled we must NOT set responseMimeType.
  // We still enforce JSON-only via SYSTEM_INSTRUCTION + schema validation/repair.
  const hasTools = Array.isArray(toolsPayload) && toolsPayload.length > 0;

  if (hasTools) {
    return { tools: toolsPayload };
  }

  // No tools: we can ask for JSON to reduce parse errors.
  // (This path mostly applies to ai_only when we intentionally don't use tools.)
  if (intentMode === 'ai_only') {
    return { responseMimeType: 'application/json' as const };
  }

  return {};
}

/**
 * Gemini chat with tool calling.
 *
 * Uses @google/genai (the newer SDK).
 */
export async function chatWithTools(
  tools: ToolDef[],
  options: ChatOptions,
  userMessage: string,
  invokeTool: (name: string, args: any) => Promise<any>
): Promise<ChatResult> {
  const ai = new GoogleGenAI({ apiKey: options.apiKey });
  const model = options.model || 'gemini-flash-latest';

  const toolsPayload = buildToolsPayload(tools);
  const toolsUsed: { name: string; args: any }[] = [];

  const intent = requiresTools(userMessage);

  const contents: any[] = [
    { role: 'user', parts: [{ text: SYSTEM_INSTRUCTION }] },
    {
      role: 'user',
      parts: [
        {
          text:
            `INTENT_MODE: ${intent.mode}\n` +
            (intent.reason ? `INTENT_REASON: ${intent.reason}\n` : '') +
            `User message:\n${userMessage}`
        }
      ]
    }
  ];

  // If the model fails to call a tool for a tool-required intent, we give it ONE explicit retry
  // to make a tool call; if it still doesn't, we hard-fail.
  let nudgedForTools = false;

  for (let i = 0; i < 10; i++) {
    const resp = await ai.models.generateContent({
      model,
      contents,
      config: buildGenerateConfig(toolsPayload, intent.mode)
    } as any);

    const calls = extractToolCalls(resp);

    if (!calls.length && intent.mode === 'tool_required' && toolsUsed.length === 0) {
      if (!nudgedForTools) {
        nudgedForTools = true;
        contents.push({
          role: 'user',
          parts: [
            {
              text:
                `You MUST call at least one tool before answering. ` +
                `Return a functionCall tool invocation next (no prose). ` +
                `Pick the best tool(s) based on the request. ` +
                `If destination is given, start with trip_search_by_destination or journal_by_tag.`
            }
          ]
        });
        continue;
      }

      const errJson = toolPolicyErrorJson(intent.reason || 'tools required', toolsUsed);
      return { text: JSON.stringify(errJson), toolsUsed, json: errJson };
    }

    if (!calls.length) {
      const text = extractText(resp);
      const validated = await validateOrRepairJson(ai, model, toolsPayload, contents, text);

      if (!validated.json) {
        const fallback = {
          type: 'error',
          request: {},
          error: {
            code: 'INVALID_MODEL_OUTPUT',
            message: 'Model did not return valid JSON for the required schema.',
            details: { raw: text }
          },
          sources: { toolsUsed: toolsUsed.map((t) => t.name) }
        };
        return { text: JSON.stringify(fallback), toolsUsed, json: fallback };
      }

      return { text: validated.text, toolsUsed, json: validated.json ?? undefined };
    }

    for (const call of calls) {
      toolsUsed.push({ name: call.name, args: call.args });
      const toolResult = await invokeTool(call.name, call.args);

      contents.push({
        role: 'tool',
        parts: [
          {
            functionResponse: {
              name: call.name,
              response: toolResult
            }
          }
        ]
      });
    }
  }

  const safety = {
    type: 'error',
    request: {},
    error: {
      code: 'TOOL_LOOP_LIMIT',
      message: 'Tool loop exceeded safety limit.'
    },
    sources: { toolsUsed: toolsUsed.map((t) => t.name) }
  };
  return { text: JSON.stringify(safety), toolsUsed, json: safety };
}
