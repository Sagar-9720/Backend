import {ToolDef} from './tools';
import {McpToolContext} from './types';

/**
 * Minimal MCP-like tool server facade.
 *
 * For now we expose tools via HTTP endpoints for debugging and to support Gemini tool execution.
 * If you want full MCP transports (stdio/sse/websocket) we can add them next.
 */
export class McpToolServer {
  constructor(private readonly tools: ToolDef[]) {}

  listTools() {
    return this.tools.map(t => ({
      name: t.name,
      description: t.description
    }));
  }

  async callTool(name: string, args: any, ctx: McpToolContext): Promise<any> {
    const tool = this.tools.find(t => t.name === name);
    if (!tool) {
      throw new Error(`Unknown tool: ${name}`);
    }

    const validatedArgs = tool.schema.parse(args || {});
    return tool.handler(validatedArgs, ctx);
  }
}

