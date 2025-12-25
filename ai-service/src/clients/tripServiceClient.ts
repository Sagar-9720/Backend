import {http} from './http';

export type TripLite = {
  id: number;
  title?: string;
  name?: string;
  destinationName?: string;
  price?: number;
};

export async function listTrips(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trips`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function getTripById(baseUrl: string, tripId: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trips/${tripId}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function tripsByDestination(baseUrl: string, destination: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trips/by-destination`, {
    params: { destination },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

