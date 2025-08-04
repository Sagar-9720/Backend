// Service implementation for Comment
import {ICommentService} from '../interfaces/commentService.interface';
import {Request} from 'express';
import Comment from '../models/comment.model';
import {CommentResponse} from '../response/response';

export class CommentService implements ICommentService {
    async createComment(req: Request): Promise<CommentResponse> {
        const {trip_id, itenary_id, destination_id, comment} = req.body;
        const authUserId = (req as any).authUser?.id;
        if (!trip_id && !itenary_id && !destination_id) {
            throw new Error('At least one of trip_id, itenary_id, or destination_id must be provided');
        }
        if (!comment || comment.trim().length === 0) {
            throw new Error('Comment text is required');
        }
        const newComment = new Comment({
            user_id: authUserId,
            trip_id,
            itenary_id,
            destination_id,
            comment: comment.trim()
        });
        const savedComment = await newComment.save();
        return {
            id: (savedComment._id as any).toString(),
            user_id: savedComment.user_id.toString(),
            trip_id: savedComment.trip_id?.toString() ?? undefined,
            itenary_id: savedComment.itenary_id?.toString() ?? undefined,
            destination_id: savedComment.destination_id?.toString() ?? undefined,
            comment: savedComment.comment,
            createdAt: savedComment.createdAt ?? new Date(),
            updatedAt: savedComment.updatedAt ?? new Date()
        };
    }

    async getComments(req: Request): Promise<CommentResponse[]> {
        const {trip_id, itenary_id, destination_id} = req.query;
        let filter: any = {};
        if (trip_id) filter.trip_id = parseInt(trip_id as string);
        if (itenary_id) filter.itenary_id = parseInt(itenary_id as string);
        if (destination_id) filter.destination_id = parseInt(destination_id as string);
        if (Object.keys(filter).length === 0) {
            throw new Error('At least one filter parameter (trip_id, itenary_id, or destination_id) is required');
        }
        const comments = await Comment.find(filter)
            .sort({createdAt: -1})
            .populate('user_id', 'name email profileImg');

        return comments.map(comment => ({
            id: (comment._id as any).toString(),
            user_id: comment.user_id.toString(),
            trip_id: comment.trip_id?.toString() ?? undefined,
            itenary_id: comment.itenary_id?.toString() ?? undefined,
            destination_id: comment.destination_id?.toString() ?? undefined,
            comment: comment.comment,
            createdAt: comment.createdAt ?? new Date(),
            updatedAt: comment.updatedAt ?? new Date()
        }));
    }

    async getCommentById(req: Request): Promise<CommentResponse> {
        const {id} = req.params;
        const comment = await Comment.findById(id)
            .populate('user_id', 'name email profileImg');
        if (!comment) {
            throw new Error('Comment not found');
        }
        return {
            id: (comment._id as any).toString(),
            user_id: comment.user_id.toString(),
            trip_id: comment.trip_id?.toString() ?? undefined,
            itenary_id: comment.itenary_id?.toString() ?? undefined,
            destination_id: comment.destination_id?.toString() ?? undefined,
            comment: comment.comment,
            createdAt: comment.createdAt ?? new Date(),
            updatedAt: comment.updatedAt ?? new Date()
        };
    }

    async updateComment(req: Request): Promise<CommentResponse> {
        const {id} = req.params;
        const {comment} = req.body;
        const authUserId = (req as any).authUser?.id;
        if (!comment || comment.trim().length === 0) {
            throw new Error('Comment text is required');
        }
        const existingComment = await Comment.findById(id);
        if (!existingComment) {
            throw new Error('Comment not found');
        }
        if (existingComment.user_id !== authUserId) {
            throw new Error('You are not authorized to update this comment');
        }
        existingComment.comment = comment.trim();
        const updatedComment = await existingComment.save();
        return {
            id: (updatedComment._id as any).toString(),
            user_id: updatedComment.user_id.toString(),
            trip_id: updatedComment.trip_id?.toString() ?? undefined,
            itenary_id: updatedComment.itenary_id?.toString() ?? undefined,
            destination_id: updatedComment.destination_id?.toString() ?? undefined,
            comment: updatedComment.comment,
            createdAt: updatedComment.createdAt ?? new Date(),
            updatedAt: updatedComment.updatedAt ?? new Date()
        };
    }

    async deleteComment(req: Request): Promise<void> {
        const {id} = req.params;
        const authUserId = (req as any).authUser?.id;
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
