// Routes for Like endpoints
import {Router} from 'express';
import {
    getLikes,
    toggleLike
} from '../controllers/like.controller';

const router = Router();

router.get('/', getLikes);
router.put('/', toggleLike)

export default router;
