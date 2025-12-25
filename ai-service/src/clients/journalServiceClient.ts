import {http} from './http';

export async function listPublicJournals(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/journal/journals/public`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function journalsByTrip(baseUrl: string, tripId: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/journal/journals/trip/${tripId}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function journalsByTag(baseUrl: string, tag: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/journal/journals/tag/${encodeURIComponent(tag)}` , {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

