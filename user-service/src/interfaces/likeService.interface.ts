import {Request} from "express";
import {LikeResponse} from "../response/response";

// Interface for Like service
export interface ILikeService {

    getLikes(req: Request): Promise<LikeResponse[]>;

    toggleLike(req: Request): Promise<{ liked: boolean; like?: LikeResponse }>;
}
