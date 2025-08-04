// Routes for Comment endpoints
import { Router } from 'express';
import {
  createComment,
  getComments,
  getCommentById,
  updateComment,
  deleteComment
} from '../controllers/comment.controller';

const router = Router();

router.post('/', createComment);
router.get('/', getComments);
router.get('/:id', getCommentById);
router.put('/:id', updateComment);
router.delete('/:id', deleteComment);

export default router;
