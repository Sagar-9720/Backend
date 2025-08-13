// Routes for SavedTrip endpoints
import {Router} from 'express';
import {
    getSavedTrips,
    toggleSavedTrip
} from '../controllers/saved_trip.controller';

const router = Router();

router.put('/', toggleSavedTrip)
router.get('/', getSavedTrips);

export default router;
