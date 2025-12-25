import {z} from 'zod';
import {Env} from '../config/env';
import {listTrips, getTripById, tripsByDestination} from '../clients/tripServiceClient';
import {listPublicJournals, journalsByTag, journalsByTrip} from '../clients/journalServiceClient';
import {getStats, getStatsBatch, StatsType} from '../clients/userServiceClient';
import {McpToolContext} from './types';

export type ToolDef = {
  name: string;
  description: string;
  schema: z.ZodTypeAny;
  // JSON schema-ish definition for Gemini function calling.
  // (We intentionally keep it simple and aligned to our zod schemas.)
  parametersJsonSchema?: any;
  handler: (args: any, ctx: McpToolContext) => Promise<any>;
};

export function buildTools(env: Env): ToolDef[] {
  return [
    {
      name: 'trip_list',
      description: 'List trips (lite). Returns the TripService response wrapper.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listTrips(env.tripServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'trip_get_by_id',
      description: 'Get a trip by id.',
      schema: z.object({tripId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          tripId: { type: 'integer', minimum: 1, description: 'Trip id' }
        },
        required: ['tripId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => getTripById(env.tripServiceBaseUrl, args.tripId, ctx.xUserInfo)
    },
    {
      name: 'trip_search_by_destination',
      description: 'Search trips by destination name (case-insensitive on backend).',
      schema: z.object({destination: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          destination: { type: 'string', minLength: 1, description: 'Destination name, e.g., Manali' }
        },
        required: ['destination'],
        additionalProperties: false
      },
      handler: async (args, ctx) => tripsByDestination(env.tripServiceBaseUrl, args.destination, ctx.xUserInfo)
    },

    {
      name: 'journal_list_public',
      description: 'List public journals.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listPublicJournals(env.journalServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'journal_by_trip',
      description: 'Get journals by trip id.',
      schema: z.object({tripId: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          tripId: { type: 'string', minLength: 1, description: 'Trip id as string' }
        },
        required: ['tripId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => journalsByTrip(env.journalServiceBaseUrl, args.tripId, ctx.xUserInfo)
    },
    {
      name: 'journal_by_tag',
      description: 'Search journals by tag (works for destinations too).',
      schema: z.object({tag: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          tag: { type: 'string', minLength: 1, description: 'Tag string, e.g., Manali' }
        },
        required: ['tag'],
        additionalProperties: false
      },
      handler: async (args, ctx) => journalsByTag(env.journalServiceBaseUrl, args.tag, ctx.xUserInfo)
    },

    {
      name: 'user_stats_get',
      description: 'Get engagement stats for a single entity (trip/journal/destination).',
      schema: z.object({
        type: z.enum(['trip', 'journal', 'destination']),
        tripId: z.string().optional(),
        journalId: z.string().optional(),
        destinationId: z.string().optional()
      }),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          type: { type: 'string', enum: ['trip', 'journal', 'destination'] },
          tripId: { type: 'string' },
          journalId: { type: 'string' },
          destinationId: { type: 'string' }
        },
        required: ['type'],
        additionalProperties: false
      },
      handler: async (args, ctx) => getStats(env.userServiceBaseUrl, {
        type: args.type as StatsType,
        tripId: args.tripId,
        journalId: args.journalId,
        destinationId: args.destinationId
      }, ctx.xUserInfo)
    },
    {
      name: 'user_stats_batch',
      description: 'Get engagement stats for many entities of the same type. (Max 50 ids).',
      schema: z.object({
        type: z.enum(['trip', 'journal', 'destination']),
        ids: z.array(z.string().min(1)).min(1).max(50)
      }),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          type: { type: 'string', enum: ['trip', 'journal', 'destination'] },
          ids: { type: 'array', items: { type: 'string', minLength: 1 }, minItems: 1, maxItems: 50 }
        },
        required: ['type', 'ids'],
        additionalProperties: false
      },
      handler: async (args, ctx) => getStatsBatch(env.userServiceBaseUrl, args.type as StatsType, args.ids, ctx.xUserInfo)
    }
  ];
}
