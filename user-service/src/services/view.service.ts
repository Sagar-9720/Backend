// Service implementation for Like
import {IViewService} from '../interfaces/viewService.interface';
import {Request} from 'express';
import {ViewResponse} from '../response/response';
import View from '../models/view.model';

export class ViewService implements IViewService {
    getViewCount(req: Request): Promise<ViewResponse | null> {
        const {trip_id, journal_id, destination_id} = req.query;

        // Validate that at least one reference ID is provided
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }

        // Use user identity from trusted source (e.g., req.authUser set by gateway/authservice)
        const authUserId = (req as any).user?.userId;

        // Build the query condition - Corrected to use proper Sequelize where syntax
        const queryCondition: any = {
            user_id: authUserId,
        };

        // Only add fields that are actually provided to avoid querying for NULL values
        if (trip_id) queryCondition.trip_id = trip_id;
        if (journal_id) queryCondition.journal_id = journal_id;
        if (destination_id) queryCondition.destination_id = destination_id;

        return View.findOne({
            where: queryCondition
        }).then(view => {
            if (!view) return null;
            return {
                id: view.id.toString(),
                user_id: view.user_id.toString(),
                trip_id: view.trip_id?.toString() ?? undefined,
                journal_id: view.journal_id?.toString() ?? undefined,
                destination_id: view.destination_id?.toString() ?? undefined,
                view_count: view.view_count,
                createdAt: view.created_at!,
                updatedAt: view.updated_at!
            };
        });
    }

    // Additional method to toggle like (like/unlike)
    async increaseViewCount(req: Request): Promise<{ increasedView: boolean; view?: ViewResponse }> {
        const {trip_id, journal_id, destination_id} = req.body;

        // Validate that at least one reference ID is provided
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }

        // Use user identity from trusted source (e.g., req.authUser set by gateway/authservice)
        const authUserId = (req as any).user?.userId;

        // Build the query condition - Corrected to use proper Sequelize syntax
        const queryCondition: any = {
            user_id: authUserId,
        };

        // Only add fields that are actually provided
        if (trip_id) queryCondition.trip_id = trip_id;
        if (journal_id) queryCondition.journal_id = journal_id;
        if (destination_id) queryCondition.destination_id = destination_id;

        // Check if the view already exists
        const existingView = await View.findOne({
            where: queryCondition
        });

        if (existingView) {
            // Increment the view count
            await existingView.increment('view_count');
            // Reload the model to get updated values
            await existingView.reload();

            return {
                increasedView: true,
                view: {
                    id: existingView.id.toString(),
                    user_id: existingView.user_id.toString(),
                    trip_id: existingView.trip_id?.toString() ?? undefined,
                    journal_id: existingView.journal_id?.toString() ?? undefined,
                    view_count: existingView.view_count,
                    destination_id: existingView.destination_id?.toString() ?? undefined,
                    createdAt: existingView.created_at!,
                    updatedAt: existingView.updated_at!
                }
            };
        } else {
            // Create a new view record
            const view = await View.create({
                user_id: authUserId,
                trip_id: trip_id || null,
                journal_id: journal_id || null,
                destination_id: destination_id || null,
                view_count: 1
            });

            return {
                increasedView: true,
                view: {
                    id: view.id.toString(),
                    user_id: view.user_id.toString(),
                    trip_id: view.trip_id?.toString() ?? undefined,
                    journal_id: view.journal_id?.toString() ?? undefined,
                    destination_id: view.destination_id?.toString() ?? undefined,
                    view_count: view.view_count,
                    createdAt: view.created_at!,
                    updatedAt: view.updated_at!
                }
            };
        }
    }
}
