// Service implementation for SavedTrip
import {ISavedTripService} from '../interfaces/savedTripService.interface';
import {Request} from 'express';
import SavedTrip from '../models/saved_trip.model';
import {SavedTripResponse} from '../response/response';

export class SavedTripService implements ISavedTripService {

    async getSavedTrips(req: Request): Promise<SavedTripResponse[]> {
        const {trip, journal, destination, user} = req.query;
        let whereClause: any = {};
        if (trip) whereClause.trip_id = parseInt(trip as string);
        if (journal) whereClause.journal_id = parseInt(journal as string);
        if (destination) whereClause.destination_id = parseInt(destination as string);
        if (user) whereClause.user_id = parseInt(user as string);
        const savedTrips = await SavedTrip.findAll({
            where: whereClause,
            order: [['created_at', 'DESC']]
        });
        return savedTrips.map(savedTrip => ({
            id: savedTrip.id.toString(),
            user_id: savedTrip.user_id.toString(),
            trip_id: savedTrip.trip_id?.toString() ?? undefined,
            journal_id: savedTrip.journal_id?.toString() ?? undefined,
            destination_id: savedTrip.destination_id?.toString() ?? undefined,
            created_at: savedTrip.created_at!,
            updated_at: savedTrip.updated_at!
        }));
    }


    // Toggle saved trip (save/unsave)
    async toggleSavedTrip(req: Request): Promise<{ saved: boolean; savedTrip?: SavedTripResponse }> {
        const {trip_id, journal_id, destination_id} = req.body;
        const authUserId = (req as any).user?.userId;
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }
        // Check if the saved trip already exists
        const existingSavedTrip = await SavedTrip.findOne({
            where: {
                user_id: authUserId,
                ...(trip_id && {trip_id}),
                ...(journal_id && {journal_id}),
                ...(destination_id && {destination_id})
            }
        });
        if (existingSavedTrip) {
            // Unsave (delete)
            await existingSavedTrip.destroy();
            return {saved: false};
        } else {
            // Save (create)
            const savedTrip = await SavedTrip.create({
                user_id: authUserId,
                trip_id: trip_id || null,
                journal_id: journal_id || null,
                destination_id: destination_id || null
            });
            return {
                saved: true,
                savedTrip: {
                    id: savedTrip.id.toString(),
                    user_id: savedTrip.user_id.toString(),
                    trip_id: savedTrip.trip_id?.toString() ?? undefined,
                    journal_id: savedTrip.journal_id?.toString() ?? undefined,
                    destination_id: savedTrip.destination_id?.toString() ?? undefined,
                    created_at: savedTrip.created_at!,
                    updated_at: savedTrip.updated_at!
                }
            };
        }
    }
}
