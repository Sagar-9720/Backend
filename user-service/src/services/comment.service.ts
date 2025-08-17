// Service implementation for Comment
import {ICommentService} from '../interfaces/commentService.interface';
import {Request} from 'express';
import Comment from '../models/comment.model';
import {CommentResponse} from '../response/response';

export class CommentService implements ICommentService {
    async createComment(req: Request): Promise<CommentResponse> {
        const {trip_id, journal_id, destination_id, comment} = req.body;
        const authUserId = (req as any).user?.userId;
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }
        if (!comment || comment.trim().length === 0) {
            throw new Error('Comment text is required');
        }
        const newComment = new Comment({
            user_id: authUserId,
            trip_id,
            journal_id,
            destination_id,
            comment: comment.trim()
        });
        const savedComment = await newComment.save();
        return {
            id: (savedComment._id as any).toString(),
            user_id: savedComment.user_id.toString(),
            trip_id: savedComment.trip_id?.toString() ?? undefined,
            journal_id: savedComment.journal_id?.toString() ?? undefined,
            destination_id: savedComment.destination_id?.toString() ?? undefined,
            comment: savedComment.comment,
            createdAt: savedComment.createdAt ?? new Date(),
            updatedAt: savedComment.updatedAt ?? new Date()
        };
    }

    async getComments(req: Request): Promise<CommentResponse[]> {
        const {trip, journal, destination} = req.query;
        let filter: any = {};
        if (trip) filter.trip_id = parseInt(trip as string);
        if (journal) filter.journal_id = parseInt(journal as string);
        if (destination) filter.destination_id = parseInt(destination as string);
        if (Object.keys(filter).length === 0) {
            throw new Error('At least one filter parameter (trip_id, journal_id, or destination_id) is required');
        }
        const comments = await Comment.find(filter)
            .sort({createdAt: -1})
            .populate('user_id', 'name email profileImg');

        return comments.map(comment => ({
            id: (comment._id as any).toString(),
            user_id: comment.user_id.toString(),
            trip_id: comment.trip_id?.toString() ?? undefined,
            journal_id: comment.journal_id?.toString() ?? undefined,
            destination_id: comment.destination_id?.toString() ?? undefined,
            comment: comment.comment,
            createdAt: comment.createdAt ?? new Date(),
            updatedAt: comment.updatedAt ?? new Date()
        }));
    }


    async deleteComment(req: Request): Promise<void> {
        const {id} = req.params;
        const authUserId = (req as any).user?.userId;
        const comment = await Comment.findById(id);
        if (!comment) {
            throw new Error('Comment not found');
        }
        if (comment.user_id !== authUserId) {
            throw new Error('You are not authorized to delete this comment');
        }
        await Comment.findByIdAndDelete(id);
    }
}
