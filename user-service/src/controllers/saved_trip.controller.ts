import {Request, Response} from 'express';
import {SavedTripService} from '../services/saved_trip.service';

const savedTripService = new SavedTripService();

// Controller for SavedTrip endpoints


export const getSavedTrips = async (req: Request, res: Response) => {
    const result = await savedTripService.getSavedTrips(req);
    res.status(200).json(result);
};

export const toggleSavedTrip = async (req: Request, res: Response) => {
    const result = await savedTripService.toggleSavedTrip(req);
    res.status(200).json(result);
};

