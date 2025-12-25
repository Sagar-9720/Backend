import {Env} from '../config/env';
import {Cache} from './cache';
import {trendingTrips, trendingJournals, trendingDestinations} from './trendingAggregations';

// Backward-compatible wrappers used by older routes.

export async function getTrendingTrips(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  return trendingTrips(env, xUserInfo, limit, cache, debug);
}

export async function getTrendingJournals(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  return trendingJournals(env, xUserInfo, limit, cache, debug);
}

export async function getTrendingDestinations(env: Env, xUserInfo: string | undefined, limit: number, cache: Cache | null, debug: boolean) {
  return trendingDestinations(env, xUserInfo, limit, cache, debug);
}
