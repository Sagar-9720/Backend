import {Env} from '../config/env';
import {listTrips} from '../clients/tripServiceClient';
import {getStatsBatch} from '../clients/userServiceClient';
import {scoreTrip} from './scoring';
import {Cache} from './cache';

export type TrendingTripItem = {
  type: 'TRIP';
  id: string;
  score: number;
  stats: {
    likes: number;
    comments: number;
    views: number;
    saves: number;
  };
  // Keep raw trip summary for now; UI can decide what fields to render.
  trip: any;
};

export async function getTrendingTrips(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  const safeLimit = Math.max(1, Math.min(limit || 10, 20));
  const cacheKey = `ai:trending:trips:v1:limit=${safeLimit}`;

  if (cache) {
    const cached = await cache.get(cacheKey);
    if (cached) {
      return JSON.parse(cached);
    }
  }

  const toolsUsed: any[] = [];

  const tripsRes = await listTrips(env.tripServiceBaseUrl, xUserInfo);
  toolsUsed.push({ tool: 'trip.listTrips', ok: true });

  // Trip service wraps data in CustomResponseEntity, so extract `data` defensively.
  const trips: any[] = Array.isArray(tripsRes) ? tripsRes : (tripsRes?.data ?? tripsRes?.result ?? []);

  // Candidate cap: avoid huge fanout.
  const candidates = trips.slice(0, 50);
  const ids = candidates
    .map(t => t?.id)
    .filter((v: any) => v !== undefined && v !== null)
    .map((v: any) => String(v));

  const stats = ids.length ? await getStatsBatch(env.userServiceBaseUrl, 'trip', ids, xUserInfo) : [];
  toolsUsed.push({ tool: 'user.getStatsBatch', count: ids.length, ok: true });

  const statsById = new Map(stats.map(s => [String(s.id), s]));

  const ranked: TrendingTripItem[] = candidates.map((trip) => {
    const id = String(trip.id);
    const s = statsById.get(id) || { type: 'trip', id, likes: 0, comments: 0, views: 0, saves: 0 };
    const score = scoreTrip(s);
    return {
      type: 'TRIP',
      id,
      score,
      stats: { likes: s.likes, comments: s.comments, views: s.views, saves: s.saves },
      trip
    };
  });

  ranked.sort((a, b) => b.score - a.score);
  const result = {
    items: ranked.slice(0, safeLimit),
    generatedAt: new Date().toISOString(),
    ...(debug ? { debug: { toolsUsed, candidateCount: candidates.length } } : {})
  };

  if (cache) {
    await cache.set(cacheKey, JSON.stringify(result), 120);
  }

  return result;
}

