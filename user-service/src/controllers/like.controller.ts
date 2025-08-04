import { Request, Response } from 'express';
import { LikeService } from '../services/like.service';

const likeService = new LikeService();

// Controller for Like endpoints
export const createLike = async (req: Request, res: Response) => {
  const result = await likeService.createLike(req);
  res.status(201).json(result);
};

export const getLikes = async (req: Request, res: Response) => {
  const result = await likeService.getLikes(req);
  res.status(200).json(result);
};

export const getLikeById = async (req: Request, res: Response) => {
  const result = await likeService.getLikeById(req);
  res.status(200).json(result);
};

export const updateLike = async (req: Request, res: Response) => {
  const result = await likeService.updateLike(req);
  res.status(200).json(result);
};

export const deleteLike = async (req: Request, res: Response) => {
  await likeService.deleteLike(req);
  res.status(204).send();
};
