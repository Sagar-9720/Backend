import { Request } from "express";
import { LikeResponse } from "../response/response";

// Interface for Like service
export interface ILikeService {
  createLike(req: Request): Promise<LikeResponse>;
  getLikes(req: Request): Promise<LikeResponse[]>;
  getLikeById(req: Request): Promise<LikeResponse>;
  updateLike(req: Request): Promise<LikeResponse>;
  deleteLike(req: Request): Promise<void>;
}
