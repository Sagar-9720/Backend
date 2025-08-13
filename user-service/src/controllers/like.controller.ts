import {Request, Response} from 'express';
import {LikeService} from '../services/like.service';

const likeService = new LikeService();


export const getLikes = async (req: Request, res: Response) => {
    const result = await likeService.getLikes(req);
    res.status(200).json(result);
};

export const toggleLike = async (req: Request, res: Response) => {
    const result = await likeService.toggleLike(req);
    res.status(200).json(result);
}
