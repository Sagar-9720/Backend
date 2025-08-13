// Routes for Comment endpoints
import {Router} from 'express';
import {
    createComment,
    getComments,
    deleteComment
} from '../controllers/comment.controller';

const router = Router();

router.post('/', createComment);
router.get('/', getComments);
router.delete('/:id', deleteComment);

export default router;
