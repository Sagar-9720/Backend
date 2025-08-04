import { Request } from "express";
import { SavedTripResponse } from '../response/response';

// Interface for SavedTrip service
export interface ISavedTripService {
  createSavedTrip(req: Request): Promise<SavedTripResponse>;
  getSavedTrips(req: Request): Promise<SavedTripResponse[]>;
  getSavedTripById(req: Request): Promise<SavedTripResponse>;
  updateSavedTrip(req: Request): Promise<SavedTripResponse>;
  deleteSavedTrip(req: Request): Promise<void>;
}
