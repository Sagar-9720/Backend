// Routes for SavedTrip endpoints
import {Router} from 'express';

import {getViews, increaseView} from "../controllers/view.controller";

const router = Router();

router.put('/', increaseView)
router.get('/', getViews);

export default router;
