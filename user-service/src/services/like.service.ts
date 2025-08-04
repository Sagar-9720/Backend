// Service implementation for Like
import {ILikeService} from '../interfaces/likeService.interface';
import {Request} from 'express';
import Like from '../models/like.model';
import {LikeResponse} from '../response/response';

export class LikeService implements ILikeService {
    async createLike(req: Request): Promise<LikeResponse> {
        const {trip_id, itinerary_id, destination_id} = req.body;
        const authUserId = (req as any).authUser?.id;
        if (!trip_id && !itinerary_id && !destination_id) {
            throw new Error('At least one of trip_id, itinerary_id, or destination_id must be provided');
        }
        const existingLike = await Like.findOne({
            where: {
                user_id: authUserId,
                ...(trip_id && {trip_id}),
                ...(itinerary_id && {itinerary_id}),
                ...(destination_id && {destination_id})
            }
        });
        if (existingLike) {
            throw new Error('Already liked');
        }
        const like = await Like.create({
            user_id: authUserId,
            trip_id: trip_id || null,
            itinerary_id: itinerary_id || null,
            destination_id: destination_id || null
        });
        return {
            id: like.id.toString(),
            user_id: like.user_id.toString(),
            trip_id: like.trip_id?.toString() ?? undefined,
            itinerary_id: like.itinerary_id?.toString() ?? undefined,
            destination_id: like.destination_id?.toString() ?? undefined,
            created_at: like.created_at!,
            updated_at: like.updated_at!
        };
    }

    async getLikes(req: Request): Promise<LikeResponse[]> {
        const authUserId = (req as any).authUser?.id;
        const {trip_id, itinerary_id, destination_id} = req.query;
        let whereClause: any = {user_id: parseInt(authUserId)};
        if (trip_id) whereClause.trip_id = parseInt(trip_id as string);
        if (itinerary_id) whereClause.itinerary_id = parseInt(itinerary_id as string);
        if (destination_id) whereClause.destination_id = parseInt(destination_id as string);
        const likes = await Like.findAll({
            where: whereClause,
            order: [['created_at', 'DESC']]
        });
        return likes.map(like => ({
            id: like.id.toString(),
            user_id: like.user_id.toString(),
            trip_id: like.trip_id?.toString() ?? undefined,
            itinerary_id: like.itinerary_id?.toString() ?? undefined,
            destination_id: like.destination_id?.toString() ?? undefined,
            created_at: like.created_at!,
            updated_at: like.updated_at!
        }));
    }

    async getLikeById(req: Request): Promise<LikeResponse> {
        const {id} = req.params;
        const like = await Like.findByPk(id);
        if (!like) {
            throw new Error('Like not found');
        }
        return {
            id: like.id.toString(),
            user_id: like.user_id.toString(),
            trip_id: like.trip_id?.toString() ?? undefined,
            itinerary_id: like.itinerary_id?.toString() ?? undefined,
            destination_id: like.destination_id?.toString() ?? undefined,
            created_at: like.created_at!,
            updated_at: like.updated_at!
        };
    }

    async updateLike(req: Request): Promise<LikeResponse> {
        const {id} = req.params;
        const {trip_id, itinerary_id, destination_id} = req.body;
        const like = await Like.findByPk(id);
        if (!like) {
            throw new Error('Like not found');
        }
        if (trip_id !== undefined) like.trip_id = trip_id;
        if (itinerary_id !== undefined) like.itinerary_id = itinerary_id;
        if (destination_id !== undefined) like.destination_id = destination_id;
        await like.save();
        return {
            id: like.id.toString(),
            user_id: like.user_id.toString(),
            trip_id: like.trip_id?.toString() ?? undefined,
            itinerary_id: like.itinerary_id?.toString() ?? undefined,
            destination_id: like.destination_id?.toString() ?? undefined,
            created_at: like.created_at!,
            updated_at: like.updated_at!
        };
    }

    async deleteLike(req: Request): Promise<void> {
        const {id} = req.params;
        const authUserId = (req as any).authUser?.id;
        const like = await Like.findByPk(id);
        if (!like) {
            throw new Error('Like not found');
        }
        if (like.user_id.toString() !== authUserId) {
            throw new Error('You are not authorized to delete this like');
        }
        await like.destroy();
    }

    // Additional method to toggle like (like/unlike)
    async toggleLike(req: Request): Promise<{ liked: boolean; like?: LikeResponse }> {
        const {trip_id, itinerary_id, destination_id} = req.body;

        // Validate that at least one reference ID is provided
        if (!trip_id && !itinerary_id && !destination_id) {
            throw new Error('At least one of trip_id, itinerary_id, or destination_id must be provided');
        }

        // Use user identity from trusted source (e.g., req.authUser set by gateway/authservice)
        const authUserId = (req as any).authUser?.id;
        // Check if the like already exists
        const existingLike = await Like.findOne({
            where: {
                user_id: authUserId,
                ...(trip_id && {trip_id}),
                ...(itinerary_id && {itinerary_id}),
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
                itinerary_id: itinerary_id || null,
                destination_id: destination_id || null
            });

            return {
                liked: true,
                like: {
                    id: like.id.toString(),
                    user_id: like.user_id.toString(),
                    trip_id: like.trip_id?.toString() ?? undefined,
                    itinerary_id: like.itinerary_id?.toString() ?? undefined,
                    destination_id: like.destination_id?.toString() ?? undefined,
                    created_at: like.created_at!,
                    updated_at: like.updated_at!
                }
            };
        }
    }
}
