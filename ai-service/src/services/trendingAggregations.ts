import {Env} from '../config/env';
import {Cache} from './cache';
import {listTrips} from '../clients/tripServiceClient';
import {listPublicJournals} from '../clients/journalServiceClient';
import {getStatsBatch, StatsResponse} from '../clients/userServiceClient';
import {scoreTrip} from './scoring';

export type TrendingEntity = 'trip' | 'journal' | 'destination';

export type TrendingItem = {
  type: 'TRIP' | 'JOURNAL' | 'DESTINATION';
  id: string;
  score: number;
  stats: { likes: number; comments: number; views: number; saves: number };
  payload?: any;
};

function statsToScore(stats: StatsResponse): number {
  // Use the same weights for now across entities.
  return 0.45 * (stats.likes || 0) + 0.25 * (stats.comments || 0) + 0.20 * (stats.views || 0) + 0.10 * (stats.saves || 0);
}

function extractDataList(res: any): any[] {
  if (Array.isArray(res)) return res;
  return (res?.data ?? res?.result ?? res?.items ?? []);
}

export async function trendingTrips(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  const safeLimit = Math.max(1, Math.min(limit || 10, 20));
  const cacheKey = `ai:trending:trip:v1:limit=${safeLimit}`;

  if (cache) {
    const cached = await cache.get(cacheKey);
    if (cached) return JSON.parse(cached);
  }

  const toolsUsed: any[] = [];

  const tripsRes = await listTrips(env.tripServiceBaseUrl, xUserInfo);
  toolsUsed.push({ tool: 'trip.listTrips', ok: true });

  const trips = extractDataList(tripsRes);
  const candidates = trips.slice(0, 50);
  const ids = candidates.map(t => t?.id).filter((v: any) => v !== undefined && v !== null).map((v: any) => String(v));

  const stats = ids.length ? await getStatsBatch(env.userServiceBaseUrl, 'trip', ids, xUserInfo) : [];
  toolsUsed.push({ tool: 'user.statsBatch', type: 'trip', count: ids.length, ok: true });
  const statsById = new Map(stats.map(s => [String(s.id), s]));

  const ranked: TrendingItem[] = candidates.map((trip) => {
    const id = String(trip.id);
    const s = statsById.get(id) || { type: 'trip', id, likes: 0, comments: 0, views: 0, saves: 0 };
    return {
      type: 'TRIP' as const,
      id,
      score: scoreTrip(s),
      stats: { likes: s.likes, comments: s.comments, views: s.views, saves: s.saves },
      payload: trip
    };
  }).sort((a, b) => b.score - a.score);

  const result = {
    entity: 'trip',
    items: ranked.slice(0, safeLimit),
    generatedAt: new Date().toISOString(),
    ...(debug ? { debug: { toolsUsed, candidateCount: candidates.length } } : {})
  };

  if (cache) await cache.set(cacheKey, JSON.stringify(result), 120);
  return result;
}

export async function trendingJournals(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  const safeLimit = Math.max(1, Math.min(limit || 10, 20));
  const cacheKey = `ai:trending:journal:v1:limit=${safeLimit}`;

  if (cache) {
    const cached = await cache.get(cacheKey);
    if (cached) return JSON.parse(cached);
  }

  const toolsUsed: any[] = [];

  const journalsRes = await listPublicJournals(env.journalServiceBaseUrl, xUserInfo);
  toolsUsed.push({ tool: 'journal.listPublic', ok: true });

  const journals = extractDataList(journalsRes);
  const candidates = journals.slice(0, 50);
  const ids = candidates.map(j => j?.id).filter((v: any) => v !== undefined && v !== null).map((v: any) => String(v));

  const stats = ids.length ? await getStatsBatch(env.userServiceBaseUrl, 'journal', ids, xUserInfo) : [];
  toolsUsed.push({ tool: 'user.statsBatch', type: 'journal', count: ids.length, ok: true });
  const statsById = new Map(stats.map(s => [String(s.id), s]));

  const ranked: TrendingItem[] = candidates.map((journal) => {
    const id = String(journal.id);
    const s = statsById.get(id) || { type: 'journal', id, likes: 0, comments: 0, views: 0, saves: 0 };
    return {
      type: 'JOURNAL' as const,
      id,
      score: statsToScore(s),
      stats: { likes: s.likes, comments: s.comments, views: s.views, saves: s.saves },
      payload: journal
    };
  }).sort((a, b) => b.score - a.score);

  const result = {
    entity: 'journal',
    items: ranked.slice(0, safeLimit),
    generatedAt: new Date().toISOString(),
    ...(debug ? { debug: { toolsUsed, candidateCount: candidates.length } } : {})
  };

  if (cache) await cache.set(cacheKey, JSON.stringify(result), 120);
  return result;
}

export async function trendingDestinations(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  const safeLimit = Math.max(1, Math.min(limit || 10, 20));
  const cacheKey = `ai:trending:destination:v1:limit=${safeLimit}`;

  if (cache) {
    const cached = await cache.get(cacheKey);
    if (cached) return JSON.parse(cached);
  }

  // We don't have a destination list endpoint in user-service; trip-service has destinations controller.
  // For MVP: infer trending destinations by grouping trips by destination_id (or destination_name when present)
  // and scoring by trip engagement.

  const toolsUsed: any[] = [];
  const tripsRes = await listTrips(env.tripServiceBaseUrl, xUserInfo);
  toolsUsed.push({ tool: 'trip.listTrips', ok: true });

  const trips = extractDataList(tripsRes);
  const candidates = trips.slice(0, 50);
  const tripIds = candidates.map(t => t?.id).filter((v: any) => v !== undefined && v !== null).map((v: any) => String(v));

  const stats = tripIds.length ? await getStatsBatch(env.userServiceBaseUrl, 'trip', tripIds, xUserInfo) : [];
  toolsUsed.push({ tool: 'user.statsBatch', type: 'trip', count: tripIds.length, ok: true });
  const statsByTripId = new Map(stats.map(s => [String(s.id), s]));

  type DestAgg = { key: string; tripCount: number; score: number; stats: {likes: number; comments: number; views: number; saves: number}; sampleTrips: any[] };
  const agg = new Map<string, DestAgg>();

  for (const trip of candidates) {
    const tripId = String(trip.id);
    const s = statsByTripId.get(tripId) || { type: 'trip', id: tripId, likes: 0, comments: 0, views: 0, saves: 0 };

    // Try common fields that may exist in TripLiteModel.
    const destinationKey = String(trip.destination_id ?? trip.destinationId ?? trip.destinationName ?? trip.destination ?? 'unknown');

    const existing = agg.get(destinationKey) || {
      key: destinationKey,
      tripCount: 0,
      score: 0,
      stats: { likes: 0, comments: 0, views: 0, saves: 0 },
      sampleTrips: [] as any[]
    };

    existing.tripCount += 1;
    existing.stats.likes += s.likes;
    existing.stats.comments += s.comments;
    existing.stats.views += s.views;
    existing.stats.saves += s.saves;
    existing.score += scoreTrip(s);

    if (existing.sampleTrips.length < 3) existing.sampleTrips.push(trip);

    agg.set(destinationKey, existing);
  }

  const ranked: TrendingItem[] = Array.from(agg.values())
    .filter(d => d.key !== 'unknown')
    .sort((a, b) => b.score - a.score)
    .slice(0, safeLimit)
    .map(d => ({
      type: 'DESTINATION' as const,
      id: d.key,
      score: d.score,
      stats: d.stats,
      payload: { tripCount: d.tripCount, sampleTrips: d.sampleTrips }
    }));

  const result = {
    entity: 'destination',
    items: ranked,
    generatedAt: new Date().toISOString(),
    ...(debug ? { debug: { toolsUsed, candidateTripCount: candidates.length, distinctDestinations: agg.size } } : {})
  };

  if (cache) await cache.set(cacheKey, JSON.stringify(result), 120);
  return result;
}

