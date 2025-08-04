// Routes for SavedTrip endpoints
import { Router } from 'express';
import {
  createSavedTrip,
  getSavedTrips,
  getSavedTripById,
  updateSavedTrip,
  deleteSavedTrip
} from '../controllers/saved_trip.controller';

const router = Router();

router.post('/', createSavedTrip);
router.get('/user/:user_id', getSavedTrips);
router.get('/:id', getSavedTripById);
router.put('/:id', updateSavedTrip);
router.delete('/:id', deleteSavedTrip);

export default router;
