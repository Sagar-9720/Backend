import {z} from 'zod';

// A structured response contract for /api/ai/chat.
// Keep this frontend-friendly and resilient to partial data.

const MoneySchema = z.object({
  currency: z.string().min(1).default('INR'),
  amount: z.number().finite().nonnegative()
});

const BudgetLineSchema = z.object({
  label: z.string().min(1),
  estimated: MoneySchema,
  notes: z.string().optional()
});

const ItineraryItemSchema = z.object({
  title: z.string().min(1),
  description: z.string().min(1),
  location: z.string().optional(),
  estimatedCost: MoneySchema.optional()
});

const ItineraryDaySchema = z.object({
  day: z.number().int().positive(),
  items: z.array(ItineraryItemSchema).min(1)
});

export const ChatStructuredResponseSchema = z
  .object({
    type: z.enum(['itinerary', 'answer', 'error']),

    // Echo user's request in a compact structured way.
    request: z
      .object({
        destination: z.string().optional(),
        days: z.number().int().positive().optional(),
        people: z.number().int().positive().optional()
      })
      .default({}),

    itinerary: z
      .object({
        destination: z.string().min(1),
        days: z.array(ItineraryDaySchema).min(1),
        notes: z.array(z.string()).optional()
      })
      .optional(),

    budget: z
      .object({
        currency: z.string().min(1).default('INR'),
        total: MoneySchema.optional(),
        perPerson: MoneySchema.optional(),
        breakdown: z.array(BudgetLineSchema).optional(),
        assumptions: z.array(z.string()).optional()
      })
      .optional(),

    error: z
      .object({
        code: z.string().min(1),
        message: z.string().min(1),
        details: z.unknown().optional()
      })
      .optional(),

    // What data sources were used.
    sources: z
      .object({
        toolsUsed: z.array(z.string()).default([]),
        note: z.string().optional()
      })
      .default({ toolsUsed: [] })
  })
  .superRefine((val, ctx) => {
    // If not an error, we require at least one tool to have been used.
    // This prevents “general knowledge” / hallucinated itineraries.
    if (val.type !== 'error' && (!val.sources?.toolsUsed || val.sources.toolsUsed.length === 0)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'toolsUsed must be non-empty for non-error responses'
      });
    }

    // Basic consistency checks
    if (val.type === 'error' && !val.error) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'error must be provided when type is error'
      });
    }
    if (val.type === 'itinerary' && !val.itinerary) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'itinerary must be provided when type is itinerary'
      });
    }
  });

// (Type export intentionally omitted for now to avoid unused-type warnings in some IDE setups.)
