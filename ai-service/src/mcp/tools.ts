import {z} from 'zod';
import {Env} from '../config/env';
import {
  listTrips,
  getTripById,
  tripsByDestination,
  tripsByPriceRange,
  suggestTrips,
  listDestinations,
  getDestinationById,
  searchDestinationsByName,
  suggestDestinations,
  destinationsByCountry,
  destinationsByRegion,
  listCountries,
  listRegions,
  getCountryById,
  getRegionById,
  listItineraries,
  itinerariesByDestination,
  suggestItineraries,
  listItineraryActivities,
  suggestItineraryActivities,
  listTripItineraryDetails
} from '../clients/tripServiceClient';
import {
  listPublicJournals,
  journalsByTag,
  journalsByTrip,
  listAllJournals,
  getJournalById,
  listTags,
  suggestTags
} from '../clients/journalServiceClient';
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
    // -------------------- TRIPS --------------------
    {
      name: 'trip_list',
      description:
        'List trips (lite). Use for general trip browsing/recommendations, or to gather candidate trip ids before ranking with user_stats_batch(type="trip"). Returns the TripService response wrapper.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listTrips(env.tripServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'trip_get_by_id',
      description:
        'Get a trip by id. Use when you already have a tripId and need full details (itinerary, pricing, destination, etc.).',
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
      description:
        'Search trips by destination name. Use when the user clearly mentions a destination and wants trips for it (e.g., “Manali trips”). For “popular/trending trips in X”, rank results using user_stats_batch(type="trip", ids=[...]).',
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
      name: 'trip_search_by_price_range',
      description:
        'Find trips within a price range. Use for questions like “trips under 10k”, “budget between 5k and 15k”. You can combine with trip_search_by_destination by filtering client-side after both calls, and optionally rank by user_stats_batch(type="trip").',
      schema: z.object({
        startPrice: z.number().nonnegative(),
        endPrice: z.number().nonnegative()
      }),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          startPrice: { type: 'number', minimum: 0 },
          endPrice: { type: 'number', minimum: 0 }
        },
        required: ['startPrice', 'endPrice'],
        additionalProperties: false
      },
      handler: async (args, ctx) => tripsByPriceRange(env.tripServiceBaseUrl, args.startPrice, args.endPrice, ctx.xUserInfo)
    },
    {
      name: 'trip_suggest',
      description:
        'Autocomplete-style suggestion for trips based on a free-text query. Use when the user is typing/searching and needs suggestions (e.g., “suggest trips for man…”).',
      schema: z.object({q: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          q: { type: 'string', minLength: 1 }
        },
        required: ['q'],
        additionalProperties: false
      },
      handler: async (args, ctx) => suggestTrips(env.tripServiceBaseUrl, args.q, ctx.xUserInfo)
    },

    // -------------------- DESTINATIONS / GEO --------------------
    {
      name: 'destination_list',
      description:
        'List all destinations. Use when the user asks “what destinations do you have?”, or when you need destination ids to call user_stats_* for destinations.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listDestinations(env.tripServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'destination_get_by_id',
      description:
        'Fetch a destination by id. Use when you already have destinationId (e.g., from destination_search_by_name) and need details.',
      schema: z.object({destinationId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { destinationId: { type: 'integer', minimum: 1 } },
        required: ['destinationId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => getDestinationById(env.tripServiceBaseUrl, args.destinationId, ctx.xUserInfo)
    },
    {
      name: 'destination_search_by_name',
      description:
        'Search destinations by name. Best for questions like “find destinations like Goa”, “do you have Manali?”. Returns matching destinations with ids.',
      schema: z.object({name: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: { name: { type: 'string', minLength: 1 } },
        required: ['name'],
        additionalProperties: false
      },
      handler: async (args, ctx) => searchDestinationsByName(env.tripServiceBaseUrl, args.name, ctx.xUserInfo)
    },
    {
      name: 'destination_suggest',
      description:
        'Autocomplete-style suggestion for destinations. Use when user provides partial query and wants destination suggestions.',
      schema: z.object({q: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: { q: { type: 'string', minLength: 1 } },
        required: ['q'],
        additionalProperties: false
      },
      handler: async (args, ctx) => suggestDestinations(env.tripServiceBaseUrl, args.q, ctx.xUserInfo)
    },
    {
      name: 'destination_by_country',
      description:
        'List destinations for a given countryId. Use when the user asks “destinations in <country>”. If you only have the country name, first list countries / find the id.',
      schema: z.object({countryId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { countryId: { type: 'integer', minimum: 1 } },
        required: ['countryId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => destinationsByCountry(env.tripServiceBaseUrl, args.countryId, ctx.xUserInfo)
    },
    {
      name: 'destination_by_region',
      description:
        'List destinations for a given regionId. Useful for region-based discovery or filtering.',
      schema: z.object({regionId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { regionId: { type: 'integer', minimum: 1 } },
        required: ['regionId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => destinationsByRegion(env.tripServiceBaseUrl, args.regionId, ctx.xUserInfo)
    },
    {
      name: 'country_list',
      description:
        'List all countries. Use to translate country name → countryId so you can fetch destinations by country.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async () => listCountries(env.tripServiceBaseUrl)
    },
    {
      name: 'country_get_by_id',
      description:
        'Get country details by id.',
      schema: z.object({countryId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { countryId: { type: 'integer', minimum: 1 } },
        required: ['countryId'],
        additionalProperties: false
      },
      handler: async (args) => getCountryById(env.tripServiceBaseUrl, args.countryId)
    },
    {
      name: 'region_list',
      description:
        'List all regions. Use to translate region name → regionId for destination_by_region filters.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async () => listRegions(env.tripServiceBaseUrl)
    },
    {
      name: 'region_get_by_id',
      description:
        'Get region details by id.',
      schema: z.object({regionId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { regionId: { type: 'integer', minimum: 1 } },
        required: ['regionId'],
        additionalProperties: false
      },
      handler: async (args) => getRegionById(env.tripServiceBaseUrl, args.regionId)
    },

    // -------------------- ITINERARIES --------------------
    {
      name: 'itinerary_list',
      description:
        'List itineraries. Use for itinerary browsing or when the user asks “show itineraries”.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listItineraries(env.tripServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'itinerary_by_destination',
      description:
        'List itineraries for a destinationId. Use when the user asks “itinerary for Goa” (first find destinationId via destination_search_by_name).',
      schema: z.object({destinationId: z.number().int().positive()}),
      parametersJsonSchema: {
        type: 'object',
        properties: { destinationId: { type: 'integer', minimum: 1 } },
        required: ['destinationId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => itinerariesByDestination(env.tripServiceBaseUrl, args.destinationId, ctx.xUserInfo)
    },
    {
      name: 'itinerary_suggest',
      description:
        'Suggest itineraries by keyword, optionally scoped to a destinationId. Use for “things to do in Goa”, “suggest itinerary for beaches”.',
      schema: z.object({
        keyword: z.string().min(1),
        destinationId: z.number().int().positive().optional()
      }),
      parametersJsonSchema: {
        type: 'object',
        properties: {
          keyword: { type: 'string', minLength: 1 },
          destinationId: { type: 'integer', minimum: 1 }
        },
        required: ['keyword'],
        additionalProperties: false
      },
      handler: async (args, ctx) => suggestItineraries(env.tripServiceBaseUrl, args.keyword, args.destinationId, ctx.xUserInfo)
    },
    {
      name: 'itinerary_activity_list',
      description:
        'List itinerary activities. Useful for browsing available activities.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listItineraryActivities(env.tripServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'itinerary_activity_suggest',
      description:
        'Suggest itinerary activities by keyword (autocomplete). Use for questions like “activities for trekking”, “things to do: scuba”.',
      schema: z.object({keyword: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: { keyword: { type: 'string', minLength: 1 } },
        required: ['keyword'],
        additionalProperties: false
      },
      handler: async (args, ctx) => suggestItineraryActivities(env.tripServiceBaseUrl, args.keyword, ctx.xUserInfo)
    },
    {
      name: 'trip_itinerary_detail_list',
      description:
        'List trip itinerary details. Mostly useful for internal/debug or administrative queries.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listTripItineraryDetails(env.tripServiceBaseUrl, ctx.xUserInfo)
    },

    // -------------------- JOURNALS --------------------
    {
      name: 'journal_list_public',
      description:
        'List public journals (feed). Use for travel stories/experiences, recent posts, or inspiration. For “trending” questions, rank journal ids with user_stats_batch(type="journal", ids=[...]).',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listPublicJournals(env.journalServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'journal_list_all',
      description:
        'List all journals visible to the current user role. Prefer journal_list_public for general discovery; use this for admin/moderation or when public-only isn\'t enough.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async (_args, ctx) => listAllJournals(env.journalServiceBaseUrl, ctx.xUserInfo)
    },
    {
      name: 'journal_get_by_id',
      description:
        'Get a full journal by id. Use for deep follow-ups once you have an id from list/search.',
      schema: z.object({journalId: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: { journalId: { type: 'string', minLength: 1 } },
        required: ['journalId'],
        additionalProperties: false
      },
      handler: async (args, ctx) => getJournalById(env.journalServiceBaseUrl, args.journalId, ctx.xUserInfo)
    },
    {
      name: 'journal_by_trip',
      description:
        'Get journals by trip id. Use when the user is looking at a specific trip and wants related stories for that exact trip.',
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
      description:
        'Search journals by tag (often destination-like). Use for topic/destination content (e.g., “Manali experiences”). For “trending destination”: use journal_list_public (or this tool for a specific tag) then rank with user_stats_batch(type="journal") and infer the most common destination tags among top journals.',
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
      name: 'journal_tag_list',
      description:
        'List all tags. Useful to see what tags exist before searching, or to map topics/places → canonical tags.',
      schema: z.object({}),
      parametersJsonSchema: { type: 'object', properties: {}, additionalProperties: false },
      handler: async () => listTags(env.journalServiceBaseUrl)
    },
    {
      name: 'journal_tag_suggest',
      description:
        'Suggest tags by query string. Use when the user provides a partial place/topic and you need the best matching tag to call journal_by_tag.',
      schema: z.object({q: z.string().min(1)}),
      parametersJsonSchema: {
        type: 'object',
        properties: { q: { type: 'string', minLength: 1 } },
        required: ['q'],
        additionalProperties: false
      },
      handler: async (args) => suggestTags(env.journalServiceBaseUrl, args.q)
    },

    // -------------------- STATS --------------------
    {
      name: 'user_stats_get',
      description:
        'Get engagement stats for a single entity. Use once you know the entity id to answer “how popular is X?”. Types: trip/journal/destination. Destination stats require destinationId (not free-text name).',
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
      description:
        'Get engagement stats for many entities of the same type (max 50). Use for “trending/most popular” by ranking trips, journals, or destinations.',
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
