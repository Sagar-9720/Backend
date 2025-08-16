// Routes for SavedTrip endpoints
import {Router} from 'express';

import {increaseView} from "../controllers/view.controller";

const router = Router();

router.put('/', increaseView)

export default router;
