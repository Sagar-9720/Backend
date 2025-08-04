import { Request } from "express";
import { CommentResponse } from '../response/response';

// Interface for Comment service
export interface ICommentService {
  createComment(req: Request): Promise<CommentResponse>;
  getComments(req: Request): Promise<CommentResponse[]>;
  getCommentById(req: Request): Promise<CommentResponse>;
  updateComment(req: Request): Promise<CommentResponse>;
  deleteComment(req: Request): Promise<void>;
}
