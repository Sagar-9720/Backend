import {describe, it, expect} from 'vitest';
import {ChatStructuredResponseSchema} from '../chatSchemas';

describe('ChatStructuredResponseSchema', () => {
  it('rejects non-error responses when toolsUsed is empty', () => {
    const parsed = ChatStructuredResponseSchema.safeParse({
      type: 'itinerary',
      request: { destination: 'Manali', days: 6, people: 3 },
      itinerary: { destination: 'Manali', days: [{ day: 1, items: [{ title: 'x', description: 'y' }] }] },
      sources: { toolsUsed: [] }
    });

    expect(parsed.success).toBe(false);
  });

  it('accepts error responses with empty toolsUsed', () => {
    const parsed = ChatStructuredResponseSchema.safeParse({
      type: 'error',
      request: {},
      error: { code: 'TOOLS_REQUIRED', message: 'no tools' },
      sources: { toolsUsed: [] }
    });

    expect(parsed.success).toBe(true);
  });

  it('accepts non-error responses when toolsUsed is non-empty', () => {
    const parsed = ChatStructuredResponseSchema.safeParse({
      type: 'answer',
      request: {},
      sources: { toolsUsed: ['trip_search_by_destination'] }
    });

    expect(parsed.success).toBe(true);
  });
});

