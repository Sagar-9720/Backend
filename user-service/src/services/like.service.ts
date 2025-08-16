// Service implementation for Like
import {ILikeService} from '../interfaces/likeService.interface';
import {Request} from 'express';
import Like from '../models/like.model';
import {LikeResponse} from '../response/response';

export class LikeService implements ILikeService {

    async getLikes(req: Request): Promise<LikeResponse[]> {
        const {trip, itinerary, destination, user} = req.query;
        let whereClause: any = {};
        if (trip) whereClause.trip_id = parseInt(trip as string);
        if (itinerary) whereClause.itinerary_id = parseInt(itinerary as string);
        if (destination) whereClause.destination_id = parseInt(destination as string);
        if (user) whereClause.user_id = parseInt(user as string);
        const likes = await Like.findAll({
            where: whereClause,
            order: [['created_at', 'DESC']]
        });
        return likes.map(like => ({
            id: like.id.toString(),
            user_id: like.user_id.toString(),
            trip_id: like.trip_id?.toString() ?? undefined,
            journal_id: like.journal_id?.toString() ?? undefined,
            destination_id: like.destination_id?.toString() ?? undefined,
            created_at: like.created_at!,
            updated_at: like.updated_at!
        }));
    }


    // Additional method to toggle like (like/unlike)
    async toggleLike(req: Request): Promise<{ liked: boolean; like?: LikeResponse }> {
        const {trip_id, journal_id, destination_id} = req.body;

        // Validate that at least one reference ID is provided
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }

        // Use user identity from trusted source (e.g., req.authUser set by gateway/authservice)
        const authUserId = (req as any).user?.userId;
        // Check if the like already exists
        const existingLike = await Like.findOne({
            where: {
                user_id: authUserId,
                ...(trip_id && {trip_id}),
                ...(journal_id && {journal_id}),
                ...(destination_id && {destination_id})
            }
        });

        if (existingLike) {
            // Unlike - delete the existing like
            await existingLike.destroy();
            return {liked: false};
        } else {
            // Like - create a new like
            const like = await Like.create({
                user_id: authUserId,
                trip_id: trip_id || null,
                journal_id: journal_id || null,
                destination_id: destination_id || null
            });

            return {
                liked: true,
                like: {
                    id: like.id.toString(),
                    user_id: like.user_id.toString(),
                    trip_id: like.trip_id?.toString() ?? undefined,
                    journal_id: like.journal_id?.toString() ?? undefined,
                    destination_id: like.destination_id?.toString() ?? undefined,
                    created_at: like.created_at!,
                    updated_at: like.updated_at!
                }
            };
        }
    }
}
