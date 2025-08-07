package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.TokenValidationResponse;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.entity.Trip;
import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.TripExistsException;
import com.travelmate.tripservice.exceptions.TripNotFoundException;
import com.travelmate.tripservice.mapper.TripMapper;
import com.travelmate.tripservice.mapper.RequestedItineraryMapper;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.repository.TripRequestRepository;
import com.travelmate.tripservice.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    private boolean isAdmin(String token) {
        try {
            TokenValidationResponse response = authServiceClient.validateToken(token);
            return response != null && response.isValid() &&
                    ("ADMIN".equalsIgnoreCase(response.getRole()) || "SUBADMIN".equalsIgnoreCase(response.getRole()));
        } catch (Exception e) {
            logger.error("AuthService validation failed", e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel createTrip(String token, TripModel tripModel) throws ItineraryNotFoundException {
        if (!isAdmin(token)) {
            throw new AccessDeniedException("Only ADMIN can create trips");
        }
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
        // Set itineraries from itineraryIds (if TripModel uses Itinerary objects, map to entities directly)
        if (tripModel.getItineraries() != null && !tripModel.getItineraries().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (Itinerary itinerary : tripModel.getItineraries()) {
                if (itinerary.getId() != null) {
                    Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itinerary.getId());
                    itineraryOpt.ifPresent(itineraries::add);
                }
            }
            trip.setItineraries(itineraries);
        }
        // Set createdBy and isActive
        trip.setCreatedBy(token); // You may want to extract userId from token
        trip.setIsActive(true);
        Trip savedTrip = tripRepository.save(trip);
        return TripMapper.toModel(savedTrip);
    }

    @Override
    @Cacheable(value = "trips", key = "#id")
    public Optional<TripModel> getTripById(Long id) throws TripNotFoundException {
        logger.info("Fetching trip by id: {}", id);
        return tripRepository.findById(id).map(TripMapper::toModel);
    }

    @Override
    @Cacheable(value = "tripsAll")
    public List<TripModel> getAllTrips() {
        logger.info("Fetching all trips");
        return tripRepository.findAll().stream().map(TripMapper::toModel).toList();
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel updateTrip(String token, TripModel updatedTripModel) throws Exception {
        if (!isAdmin(token)) {
            throw new AccessDeniedException("Only ADMIN can update trips");
        }
        Long id = updatedTripModel.getId();
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
        // Set itineraries from Itinerary objects
        if (updatedTripModel.getItineraries() != null && !updatedTripModel.getItineraries().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (Itinerary itinerary : updatedTripModel.getItineraries()) {
                if (itinerary.getId() != null) {
                    Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itinerary.getId());
                    itineraryOpt.ifPresent(itineraries::add);
                }
            }
            existingTrip.setItineraries(itineraries);
        }
        Trip savedTrip = tripRepository.save(existingTrip);
        return TripMapper.toModel(savedTrip);
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel deleteTrip(String token, Long id) throws Exception {
        if (!isAdmin(token)) {
            throw new AccessDeniedException("Only ADMIN can delete trips");
        }
        logger.info("Deleting trip id: {}", id);
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        trip.setIsActive(false); // Soft delete
        tripRepository.save(trip);
        return TripMapper.toModel(trip);
    }

    @Override
    public List<TripModel> getTripRequestByUserId(String userId) throws Exception {
        logger.info("Fetching trip requests by user id: {}", userId);
        // Example: fetch all trips created by this user
        List<Trip> trips = tripRepository.findAll();
        List<TripModel> userTrips = new ArrayList<>();
        for (Trip trip : trips) {
            if (userId.equals(trip.getCreatedBy())) {
                userTrips.add(TripMapper.toModel(trip));
            }
        }
        return userTrips;
    }

    @Override
    @Cacheable(value = "tripsByDestination", key = "#destinationName")
    public List<TripModel> getTripsByDestinationName(String destinationName) {
        logger.info("Fetching trips by destination name: {}", destinationName);
        return tripRepository.findByMainDestinationContainingIgnoreCase(destinationName)
                .stream()
                .map(TripMapper::toModel)
                .toList();
    }

    @Override
    @Cacheable(value = "tripsByPrice", key = "#startPrice.toString().concat('-').concat(#endPrice.toString())")
    public List<TripModel> tripsBtwPriceRanges(BigDecimal startPrice, BigDecimal endPrice) {
        logger.info("Fetching trips between price range: {} - {}", startPrice, endPrice);
        return tripRepository.findByPriceBetween(startPrice, endPrice)
                .stream()
                .map(TripMapper::toModel)
                .toList();
    }

   @Override
   public void autoDeleteTripByDate() {
       List<Trip> trips = tripRepository.findAll();
       for (Trip trip : trips) {
           if (trip.getEndDate() != null && trip.getEndDate().toLocalDate().isBefore(java.time.LocalDate.now()) && Boolean.TRUE.equals(trip.getIsActive())) {
               trip.setIsActive(false);
               tripRepository.save(trip);
           }
       }
   }

    @Override
    public List<TripModel> addTripsRequestedByUser(String token, TripModel tripModel) throws Exception {
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new AccessDeniedException("Unauthorized access");
        }
        String userName = authServiceClient.validateToken(token).getUsername();

        logger.info("Adding trips requested by user: {}", tripModel.getTitle());
        List<TripRequest.RequestedItinerary> requestedItineraries = new ArrayList<>();
        if (tripModel.getItineraries() != null) {
            for (Itinerary itinerary : tripModel.getItineraries()) {
                requestedItineraries.add(RequestedItineraryMapper.toRequestedItinerary(itinerary));
            }
        }
        TripRequest request = TripRequest.builder()
                .title(tripModel.getTitle())
                .description(tripModel.getDescription())
                .startDate(tripModel.getStartDate())
                .endDate(tripModel.getEndDate())
                .price(tripModel.getPrice())
                .mainDestinationId(tripModel.getMainDestinationId())
                .requestedBy(userName)
                .approved(false)
                .itineraries(requestedItineraries)
                .build();
        TripRequest savedRequest = tripRequestRepository.save(request);
        TripModel model = TripModel.builder()
                .id(savedRequest.getId() != null ? Long.valueOf(savedRequest.getId()) : null)
                .title(savedRequest.getTitle())
                .description(savedRequest.getDescription())
                .startDate(savedRequest.getStartDate())
                .endDate(savedRequest.getEndDate())
                .price(savedRequest.getPrice())
                .mainDestinationId(savedRequest.getMainDestinationId())
                .itineraries(tripModel.getItineraries())
                .build();
        return List.of(model);
    }

    public TripModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws Exception {
        if (!isAdmin(token)) {
            throw new AccessDeniedException("Only ADMIN or SUBADMIN can approve trip requests");
        }
        TripRequest request = tripRequestRepository.findById(tripRequestId)
                .orElseThrow(() -> new RuntimeException("TripRequest not found"));
        if (Boolean.TRUE.equals(request.getApproved())) {
            throw new RuntimeException("TripRequest already approved");
        }
        // Map the provided TripRequest (tripRequest param) to TripModel
        TripModel tripModel = TripModel.builder()
                .title(tripRequest.getTitle())
                .description(tripRequest.getDescription())
                .startDate(tripRequest.getStartDate())
                .endDate(tripRequest.getEndDate())
                .price(tripRequest.getPrice())
                .mainDestinationId(tripRequest.getMainDestinationId())
                .createdBy(tripRequest.getRequestedBy())
                .itineraries(tripRequest.getItineraries().stream().map(RequestedItineraryMapper::toItinerary).toList())
                .build();
        TripModel createdTrip = createTrip(token, tripModel);
        request.setApproved(true);
        logger.info("Deleting approved TripRequest: id={}, title={}, requestedBy={}", request.getId(), request.getTitle(), request.getRequestedBy());
        tripRequestRepository.delete(request);
        return createdTrip;
    }
}
