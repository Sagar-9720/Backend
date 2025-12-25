import {http} from './http';

export type StatsType = 'trip' | 'journal' | 'destination';

export type StatsResponse = {
  type: StatsType;
  id: string;
  likes: number;
  comments: number;
  views: number;
  saves: number;
};

export async function getStats(baseUrl: string, args: {type: StatsType; tripId?: string; journalId?: string; destinationId?: string}, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/users/stats`, {
    params: args,
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data as StatsResponse;
}

export async function getStatsBatch(baseUrl: string, type: StatsType, ids: string[], xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/users/stats/batch`, {
    params: { type, ids: ids.join(',') },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data as StatsResponse[];
}

