// Routes for Like endpoints
import { Router } from 'express';
import {
  createLike,
  getLikes,
  getLikeById,
  updateLike,
  deleteLike
} from '../controllers/like.controller';

const router = Router();

router.post('/', createLike);
router.get('/', getLikes);
router.get('/:id', getLikeById);
router.put('/:id', updateLike);
router.delete('/:id', deleteLike);

export default router;
