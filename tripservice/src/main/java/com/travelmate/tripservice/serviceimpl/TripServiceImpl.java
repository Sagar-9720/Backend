package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.domain.Destination;
import com.travelmate.tripservice.domain.Itinerary;
import com.travelmate.tripservice.domain.Trip;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.TripExistsException;
import com.travelmate.tripservice.exceptions.TripNotFoundException;
import com.travelmate.tripservice.mapper.TripMapper;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.service.DestinationService;
import com.travelmate.tripservice.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @Override
    public TripModel createTrip(TripModel tripModel) throws ItineraryNotFoundException, TripExistsException {
        logger.info("Creating trip: {}", tripModel.getTitle());
        List<Trip> existing = tripRepository.findByTitleContainingIgnoreCase(tripModel.getTitle());
        if (!existing.isEmpty()) {
            throw new TripExistsException(tripModel.getTitle());
        }
        Trip trip = TripMapper.toEntity(tripModel);
        // Set main destination from ID
        if (tripModel.getMainDestinationId() != null) {
            Optional<Destination> mainDestOpt = destinationRepository.findById(tripModel.getMainDestinationId());
            if (mainDestOpt.isPresent()) {
                trip.setMainDestination(mainDestOpt.get());
            } else {
                throw new RuntimeException("Main destination not found");
            }
        }
        // Set itineraries from itineraryIds
        if (tripModel.getItineraryIds() != null && !tripModel.getItineraryIds().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (Long itineraryId : tripModel.getItineraryIds()) {
                Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itineraryId);
                if (itineraryOpt.isPresent()) {
                    itineraries.add(itineraryOpt.get());
                } else {
                    throw new ItineraryNotFoundException(itineraryId);
                }
            }
            trip.setItineraries(itineraries);
        }
        Trip savedTrip = tripRepository.save(trip);
        return TripMapper.toModel(savedTrip);
    }

    @Override
    public Optional<TripModel> getTripById(Long id) throws TripNotFoundException {
        logger.info("Fetching trip by id: {}", id);
        return tripRepository.findById(id).map(TripMapper::toModel);
    }

    @Override
    public List<TripModel> getAllTrips() {
        logger.info("Fetching all trips");
        return tripRepository.findAll().stream().map(TripMapper::toModel).toList();
    }

    @Override
    public TripModel updateTrip(Long id, TripModel updatedTripModel) throws TripNotFoundException, ItineraryNotFoundException {
        logger.info("Updating trip id: {}", id);
        Trip existingTrip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        existingTrip.setTitle(updatedTripModel.getTitle());
        existingTrip.setDescription(updatedTripModel.getDescription());
        existingTrip.setStartDate(updatedTripModel.getStartDate());
        existingTrip.setEndDate(updatedTripModel.getEndDate());
        existingTrip.setPrice(updatedTripModel.getPrice());
        // Set main destination from ID
        if (updatedTripModel.getMainDestinationId() != null) {
            Optional<Destination> mainDestOpt = destinationRepository.findById(updatedTripModel.getMainDestinationId());
            if (mainDestOpt.isPresent()) {
                existingTrip.setMainDestination(mainDestOpt.get());
            } else {
                throw new RuntimeException("Main destination not found");
            }
        }
        // Set itineraries from itineraryIds
        if (updatedTripModel.getItineraryIds() != null && !updatedTripModel.getItineraryIds().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (Long itineraryId : updatedTripModel.getItineraryIds()) {
                Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itineraryId);
                if (itineraryOpt.isPresent()) {
                    itineraries.add(itineraryOpt.get());
                } else {
                    throw new ItineraryNotFoundException(itineraryId);
                }
            }
            existingTrip.setItineraries(itineraries);
        }
        Trip savedTrip = tripRepository.save(existingTrip);
        return TripMapper.toModel(savedTrip);
    }

    @Override
    public void deleteTrip(Long id) throws TripNotFoundException {
        logger.info("Deleting trip id: {}", id);
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        tripRepository.delete(trip);
    }

    @Override
    public List<TripModel> getTripsByDestinationName(String destinationName) {
        logger.info("Fetching trips by destination name: {}", destinationName);
        return tripRepository.findByMainDestinationContainingIgnoreCase(destinationName)
            .stream()
            .map(TripMapper::toModel)
            .toList();
    }

    @Override
    public List<TripModel> tripsBtwPriceRanges(BigDecimal startPrice, BigDecimal endPrice) {
        logger.info("Fetching trips between price range: {} - {}", startPrice, endPrice);
        return tripRepository.findByPriceBetween(startPrice, endPrice)
            .stream()
            .map(TripMapper::toModel)
            .toList();
    }

    @Override
    public void autoDeleteTripByDate(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        if (trip.getEndDate() != null && trip.getEndDate().isBefore(LocalDate.now().atStartOfDay())) {
            tripRepository.delete(trip);
        }
    }
}
