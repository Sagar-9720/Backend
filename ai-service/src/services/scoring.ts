import {StatsResponse} from '../clients/userServiceClient';

export function scoreTrip(stats: StatsResponse): number {
  // Simple weighted sum. In later iterations we can normalize and add recency.
  return (
    0.45 * (stats.likes || 0) +
    0.25 * (stats.comments || 0) +
    0.20 * (stats.views || 0) +
    0.10 * (stats.saves || 0)
  );
}

