import {Request} from 'express';
import Like from '../models/like.model';
import SavedTrip from '../models/saved_trip.model';
import View from '../models/view.model';
import Comment from '../models/comment.model';
import {IStatsService} from '../interfaces/statsService.interface';

export type ContentType = 'trip' | 'journal' | 'destination';

export type StatsResponse = {
    type: ContentType;
    id: string;
    likes: number;
    comments: number;
    views: number;
    saves: number;
};

/**
 * StatsService
 *
 * Lightweight aggregation endpoints used by ai-service/MCP to rank content.
 *
 * Note:
 * - likes/saves/views are stored in Postgres via Sequelize models.
 * - comments are stored in MongoDB (Mongoose).
 */
export class StatsService implements IStatsService {

    async getStats(req: Request): Promise<StatsResponse> {
        const {type, tripId, journalId, destinationId} = req.query;

        const parsedType = String(type || '').trim() as ContentType;
        if (!parsedType || !['trip', 'journal', 'destination'].includes(parsedType)) {
            throw new Error("Query param 'type' must be one of: trip, journal, destination");
        }

        const idsProvided = [tripId, journalId, destinationId].filter(v => v !== undefined);
        if (idsProvided.length !== 1) {
            throw new Error("Provide exactly one identifier query param: tripId OR journalId OR destinationId");
        }

        // Build where conditions for SQL tables
        const where: any = {};
        // Build filter for Mongo comments
        const commentFilter: any = {};

        let id: string;

        if (tripId !== undefined) {
            if (parsedType !== 'trip') {
                throw new Error("'type=trip' is required when using tripId");
            }
            const trip_id = parseInt(String(tripId), 10);
            if (Number.isNaN(trip_id)) throw new Error('tripId must be a number');
            where.trip_id = trip_id;
            commentFilter.trip_id = trip_id;
            id = String(trip_id);
        } else if (destinationId !== undefined) {
            if (parsedType !== 'destination') {
                throw new Error("'type=destination' is required when using destinationId");
            }
            const destination_id = parseInt(String(destinationId), 10);
            if (Number.isNaN(destination_id)) throw new Error('destinationId must be a number');
            where.destination_id = destination_id;
            commentFilter.destination_id = destination_id;
            id = String(destination_id);
        } else {
            // journalId
            if (parsedType !== 'journal') {
                throw new Error("'type=journal' is required when using journalId");
            }
            const journal_id = String(journalId);
            if (!journal_id) throw new Error('journalId must be a non-empty string');
            where.journal_id = journal_id;
            commentFilter.journal_id = journal_id;
            id = journal_id;
        }

        // Execute counts in parallel
        const [likes, saves, viewsSum, comments] = await Promise.all([
            Like.count({where}),
            SavedTrip.count({where}),
            View.sum('view_count', {where}).then(v => Number(v || 0)),
            Comment.countDocuments(commentFilter)
        ]);

        return {
            type: parsedType,
            id,
            likes: Number(likes || 0),
            comments: Number(comments || 0),
            views: Number(viewsSum || 0),
            saves: Number(saves || 0)
        };
    }

    /**
     * Batch stats is optional but very helpful for ai-service to avoid N calls.
     *
     * Example:
     *  /api/users/stats/batch?type=trip&ids=1,2,3
     */
    async getStatsBatch(req: Request): Promise<StatsResponse[]> {
        const {type, ids} = req.query;

        const parsedType = String(type || '').trim() as ContentType;
        if (!parsedType || !['trip', 'journal', 'destination'].includes(parsedType)) {
            throw new Error("Query param 'type' must be one of: trip, journal, destination");
        }

        const rawIds = String(ids || '').trim();
        if (!rawIds) {
            throw new Error("Query param 'ids' is required (comma-separated)");
        }

        const idList = rawIds.split(',').map(s => s.trim()).filter(Boolean);
        if (idList.length === 0) {
            throw new Error("Query param 'ids' must contain at least one id");
        }

        // Hard limit to avoid abuse
        if (idList.length > 50) {
            throw new Error('Max 50 ids per request');
        }

        const tasks = idList.map(id => {
            const fakeReq: any = {query: {type: parsedType}};
            if (parsedType === 'trip') fakeReq.query.tripId = id;
            if (parsedType === 'destination') fakeReq.query.destinationId = id;
            if (parsedType === 'journal') fakeReq.query.journalId = id;
            return this.getStats(fakeReq as Request);
        });

        return Promise.all(tasks);
    }
}
