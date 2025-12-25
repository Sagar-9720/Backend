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

export async function tripsByPriceRange(baseUrl: string, startPrice: number, endPrice: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trips/by-price-range`, {
    params: { startPrice, endPrice },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function suggestTrips(baseUrl: string, q: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trips/suggest`, {
    params: { q },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function listDestinations(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function getDestinationById(baseUrl: string, id: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations/${id}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function searchDestinationsByName(baseUrl: string, name: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations/search`, {
    params: { name },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function suggestDestinations(baseUrl: string, q: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations/suggest`, {
    params: { q },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function destinationsByRegion(baseUrl: string, regionId: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations/region/${regionId}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function destinationsByCountry(baseUrl: string, countryId: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/destinations/country/${countryId}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function listCountries(baseUrl: string) {
  const res = await http.get(`${baseUrl}/api/trip/countries`);
  return res.data;
}

export async function listRegions(baseUrl: string) {
  const res = await http.get(`${baseUrl}/api/trip/regions`);
  return res.data;
}

export async function getCountryById(baseUrl: string, id: number) {
  const res = await http.get(`${baseUrl}/api/trip/countries/${id}`);
  return res.data;
}

export async function getRegionById(baseUrl: string, id: number) {
  const res = await http.get(`${baseUrl}/api/trip/regions/${id}`);
  return res.data;
}

export async function listItineraries(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/itineraries`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function itinerariesByDestination(baseUrl: string, destinationId: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/itineraries/destination/${destinationId}`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function suggestItineraries(baseUrl: string, keyword: string, destinationId?: number, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/itineraries/suggest`, {
    params: destinationId ? { keyword, destinationId } : { keyword },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function listItineraryActivities(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/itinerary-activities`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function suggestItineraryActivities(baseUrl: string, keyword: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/itinerary-activities/suggest`, {
    params: { keyword },
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}

export async function listTripItineraryDetails(baseUrl: string, xUserInfo?: string) {
  const res = await http.get(`${baseUrl}/api/trip/trip-itinerary-details`, {
    headers: xUserInfo ? { 'X-UserInfo': xUserInfo } : undefined
  });
  return res.data;
}
