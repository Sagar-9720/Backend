import {Request, Response} from 'express';

import {CommentService} from '../services/comment.service';

const commentService = new CommentService();

// Controller for Comment endpoints
export const createComment = async (req: Request, res: Response) => {
    const result = await commentService.createComment(req);
    res.status(201).json(result);
};

export const getComments = async (req: Request, res: Response) => {
    const result = await commentService.getComments(req);
    res.status(200).json(result);
};
export const deleteComment = async (req: Request, res: Response) => {
    await commentService.deleteComment(req);
    res.status(204).send();
};
