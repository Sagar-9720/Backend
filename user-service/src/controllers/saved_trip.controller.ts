import { Request, Response } from 'express';
import { SavedTripService } from '../services/saved_trip.service';

const savedTripService = new SavedTripService();

// Controller for SavedTrip endpoints
export const createSavedTrip = async (req: Request, res: Response) => {
  const result = await savedTripService.createSavedTrip(req);
  res.status(201).json(result);
};

export const getSavedTrips = async (req: Request, res: Response) => {
  const result = await savedTripService.getSavedTrips(req);
  res.status(200).json(result);
};

export const getSavedTripById = async (req: Request, res: Response) => {
  const result = await savedTripService.getSavedTripById(req);
  res.status(200).json(result);
};

export const updateSavedTrip = async (req: Request, res: Response) => {
  const result = await savedTripService.updateSavedTrip(req);
  res.status(200).json(result);
};

export const deleteSavedTrip = async (req: Request, res: Response) => {
  await savedTripService.deleteSavedTrip(req);
  res.status(204).send();
};
