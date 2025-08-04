// Service implementation for SavedTrip
import { ISavedTripService } from '../interfaces/savedTripService.interface';
import { Request } from 'express';
import SavedTrip from '../models/saved_trip.model';
import { SavedTripResponse } from '../response/response';

export class SavedTripService implements ISavedTripService {
  async createSavedTrip(req: Request): Promise<SavedTripResponse> {
    const { trip_id, itinerary_id, destination_id } = req.body;
    const authUserId = (req as any).authUser?.id;
    if (!trip_id && !itinerary_id && !destination_id) {
      throw new Error('At least one of trip_id, itinerary_id, or destination_id must be provided');
    }
    const existingSavedTrip = await SavedTrip.findOne({
      where: {
        user_id: authUserId,
        ...(trip_id && { trip_id }),
        ...(itinerary_id && { itinerary_id }),
        ...(destination_id && { destination_id })
      }
    });
    if (existingSavedTrip) {
      throw new Error('Trip is already saved');
    }
    const savedTrip = await SavedTrip.create({
      user_id: authUserId,
      trip_id: trip_id || null,
      itinerary_id: itinerary_id || null,
      destination_id: destination_id || null
    });
    return {
      id: savedTrip.id.toString(),
      user_id: savedTrip.user_id.toString(),
      trip_id: savedTrip.trip_id?.toString() ?? undefined,
      itinerary_id: savedTrip.itinerary_id?.toString() ?? undefined,
      destination_id: savedTrip.destination_id?.toString() ?? undefined,
      created_at: savedTrip.created_at!,
      updated_at: savedTrip.updated_at!
    };
  }

  async getSavedTrips(req: Request): Promise<SavedTripResponse[]> {
    const authUserId = (req as any).authUser?.id;
    const { type } = req.query;
    let whereClause: any = { user_id: parseInt(authUserId) };
    if (type === 'trip') {
      whereClause.trip_id = { [require('sequelize').Op.ne]: null };
    } else if (type === 'itinerary') {
      whereClause.itinerary_id = { [require('sequelize').Op.ne]: null };
    } else if (type === 'destination') {
      whereClause.destination_id = { [require('sequelize').Op.ne]: null };
    }
    const savedTrips = await SavedTrip.findAll({
      where: whereClause,
      order: [['created_at', 'DESC']]
    });
    return savedTrips.map(savedTrip => ({
      id: savedTrip.id.toString(),
      user_id: savedTrip.user_id.toString(),
      trip_id: savedTrip.trip_id?.toString() ?? undefined,
      itinerary_id: savedTrip.itinerary_id?.toString() ?? undefined,
      destination_id: savedTrip.destination_id?.toString() ?? undefined,
      created_at: savedTrip.created_at!,
      updated_at: savedTrip.updated_at!
    }));
  }

  async getSavedTripById(req: Request): Promise<SavedTripResponse> {
    const { id } = req.params;
    const savedTrip = await SavedTrip.findByPk(id);
    if (!savedTrip) {
      throw new Error('Saved trip not found');
    }
    return {
      id: savedTrip.id.toString(),
      user_id: savedTrip.user_id.toString(),
      trip_id: savedTrip.trip_id?.toString() ?? undefined,
      itinerary_id: savedTrip.itinerary_id?.toString() ?? undefined,
      destination_id: savedTrip.destination_id?.toString() ?? undefined,
      created_at: savedTrip.created_at!,
      updated_at: savedTrip.updated_at!
    };
  }

  async updateSavedTrip(req: Request): Promise<SavedTripResponse> {
    const { id } = req.params;
    const { trip_id, itinerary_id, destination_id } = req.body;
    const savedTrip = await SavedTrip.findByPk(id);
    if (!savedTrip) {
      throw new Error('Saved trip not found');
    }
    if (trip_id !== undefined) savedTrip.trip_id = trip_id;
    if (itinerary_id !== undefined) savedTrip.itinerary_id = itinerary_id;
    if (destination_id !== undefined) savedTrip.destination_id = destination_id;
    await savedTrip.save();
    return {
      id: savedTrip.id.toString(),
      user_id: savedTrip.user_id.toString(),
      trip_id: savedTrip.trip_id?.toString() ?? undefined,
      itinerary_id: savedTrip.itinerary_id?.toString() ?? undefined,
      destination_id: savedTrip.destination_id?.toString() ?? undefined,
      created_at: savedTrip.created_at!,
      updated_at: savedTrip.updated_at!
    };
  }

  async deleteSavedTrip(req: Request): Promise<void> {
    const { id } = req.params;
    const savedTrip = await SavedTrip.findByPk(id);
    if (!savedTrip) {
      throw new Error('Saved trip not found');
    }
    await savedTrip.destroy();
  }
}
