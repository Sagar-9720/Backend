import {Request} from "express";
import {SavedTripResponse} from '../response/response';

// Interface for SavedTrip service
export interface ISavedTripService {

    getSavedTrips(req: Request): Promise<SavedTripResponse[]>;

    toggleSavedTrip(req: Request): Promise<{ saved: boolean; savedTrip?: SavedTripResponse }>;
}
