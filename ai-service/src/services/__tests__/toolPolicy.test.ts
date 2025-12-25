import {describe, it, expect, vi} from 'vitest';
import {chatWithTools} from '../geminiChatService';

// This test stubs the GoogleGenAI constructor so we can simulate a model response
// that does NOT call tools, and verify we hard-fail when the intent requires tools.

describe('chatWithTools tool-first policy', () => {
  it('hard-fails with TOOLS_REQUIRED error when intent requires tools but model calls none', async () => {
    vi.resetModules();

    // Dynamic import after mocking to ensure the module sees the mock.
    vi.mock('@google/genai', () => {
      class GoogleGenAI {
        models = {
          generateContent: vi.fn(async () => ({ candidates: [{ content: { parts: [{ text: '{"oops":true}' }] } }] }))
        };
        constructor(_opts: any) {}
      }
      return { GoogleGenAI };
    });

    const {chatWithTools: chatWithToolsMocked} = await import('../geminiChatService');

    const result = await chatWithToolsMocked(
      [],
      { apiKey: 'test-key', model: 'test-model' },
      'Create a trip for me for manali for 5-7 days for 3 people give me the estimated budget for it',
      async () => ({ ok: true })
    );

    expect(result.json).toMatchObject({
      type: 'error',
      error: { code: 'TOOLS_REQUIRED' }
    });
  });
});

