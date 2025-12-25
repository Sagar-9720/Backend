import {Router} from 'express';
import {getStats, getStatsBatch} from '../controllers/stats.controller';

const router = Router();

router.get('/', getStats);
router.get('/batch', getStatsBatch);

export default router;

