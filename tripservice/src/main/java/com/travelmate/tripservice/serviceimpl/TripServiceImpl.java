package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.TokenValidationResponse;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.entity.Trip;
import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.mapper.TripMapper;
import com.travelmate.tripservice.mapper.RequestedItineraryMapper;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.repository.TripRequestRepository;
import com.travelmate.tripservice.service.TokenValidationService;
import com.travelmate.tripservice.service.TripService;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Value("${elasticsearch.index.trips:trips}")
    private String tripIndex;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    private String isAdmin(String token) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        TokenValidationResponse response = validateToken(token);
        if ("ADMIN".equalsIgnoreCase(response.getRole()) || "SUBADMIN".equalsIgnoreCase(response.getRole())) {
            return response.getUsername();
        }
        throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            // Use cache-backed validation
            if (!tokenValidationService.isTokenValid(token)) {
                return null;
            }
            return authServiceClient.validateTokenExtracted(token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel createTrip(String token, TripModel tripModel) throws TripExistsException, UnauthorizedAccessException {
        String userName = isAdmin(token);
        logger.info("Creating trip: {}", tripModel.title());
        List<Trip> existing = tripRepository.findByTitleContainingIgnoreCase(tripModel.title());
        if (!existing.isEmpty()) {
            throw new TripExistsException(tripModel.title());
        }
        Trip trip = TripMapper.toEntity(tripModel);
        // Set main destination from ID
        if (tripModel.mainDestinationId() != null) {
            Optional<Destination> mainDestOpt = destinationRepository.findById(tripModel.mainDestinationId());
            if (mainDestOpt.isPresent()) {
                trip.setMainDestination(mainDestOpt.get());
            } else {
                throw new DestinationNotFoundException(tripModel.mainDestinationId());
            }
        }
        // Set itineraries from itineraryIds (if TripModel uses Itinerary objects, map to entities directly)
        if (tripModel.itineraries() != null && !tripModel.itineraries().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (ItineraryModel itinerary : tripModel.itineraries()) {
                if (itinerary.id() != null) {
                    Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itinerary.id());
                    itineraryOpt.ifPresent(itineraries::add);
                }
            }
            trip.setItineraries(itineraries);
        }

        trip.setCreatedBy(userName);
        trip.setIsActive(true);
        Trip savedTrip = tripRepository.save(trip);
        indexTrip(TripMapper.toModel(savedTrip));
        return TripMapper.toModel(savedTrip);
    }

    @Override
    @Cacheable(value = "trips", key = "#id")
    public TripModel getTripById(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trip by id: {}", id);
        return tripRepository.findById(id).map(TripMapper::toModel).orElseThrow(() -> new TripNotFoundException(id));
    }

    @Override
    @Cacheable(value = "tripsAll")
    public List<TripModel> getAllTrips(String token) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching all trips");
        return tripRepository.findAll().stream().map(TripMapper::toModel).toList();
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel updateTrip(String token, TripModel updatedTripModel) throws TripNotFoundException, UnauthorizedAccessException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        Long id = updatedTripModel.id();
        logger.info("Updating trip id: {}", id);
        Trip existingTrip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        existingTrip.setTitle(updatedTripModel.title());
        existingTrip.setDescription(updatedTripModel.description());
        existingTrip.setStartDate(updatedTripModel.startDate());
        existingTrip.setEndDate(updatedTripModel.endDate());
        existingTrip.setPrice(updatedTripModel.price());

        // Set main destination from ID
        if (updatedTripModel.mainDestinationId() != null) {
            Optional<Destination> mainDestOpt = destinationRepository.findById(updatedTripModel.mainDestinationId());
            if (mainDestOpt.isPresent()) {
                existingTrip.setMainDestination(mainDestOpt.get());
            } else {
                throw new RuntimeException("Main destination not found");
            }
        }
        // Set itineraries from Itinerary objects
        if (updatedTripModel.itineraries() != null && !updatedTripModel.itineraries().isEmpty()) {
            List<Itinerary> itineraries = new ArrayList<>();
            for (ItineraryModel itinerary : updatedTripModel.itineraries()) {
                if (itinerary.id() != null) {
                    Optional<Itinerary> itineraryOpt = itineraryRepository.findById(itinerary.id());
                    itineraryOpt.ifPresent(itineraries::add);
                }
            }
            existingTrip.setItineraries(itineraries);
        }
        Trip savedTrip = tripRepository.save(existingTrip);
        indexTrip(TripMapper.toModel(savedTrip)); // Re-index the updated trip in Elasticsearch
        return TripMapper.toModel(savedTrip);
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripModel deleteTrip(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        logger.info("Disabling trip id: {}", id);
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        trip.setIsActive(false);
        tripRepository.save(trip);
        return TripMapper.toModel(trip);
    }

    @Override
    public List<TripModel> getTripRequestByUserId(String token, String userId) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trip requests by user id: {}", userId);
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
    public List<TripModel> getTripsByDestinationName(String token, String destinationName) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trips by destination name: {}", destinationName);
        if (destinationRepository.findByNameContainingIgnoreCase(destinationName) == null) {
            throw new DestinationNotFoundException(destinationName);
        }
        return tripRepository.findByMainDestinationContainingIgnoreCase(destinationName).stream().map(TripMapper::toModel).toList();
    }

    @Override
    @Cacheable(value = "tripsByPrice", key = "#startPrice.toString().concat('-').concat(#endPrice.toString())")
    public List<TripModel> tripsBtwPriceRanges(String token, BigDecimal startPrice, BigDecimal endPrice) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trips between price range: {} - {}", startPrice, endPrice);
        return tripRepository.findByPriceBetween(startPrice, endPrice).stream().map(TripMapper::toModel).toList();
    }

    @Override
    public void autoDeleteTripByDate(String token) throws UnauthorizedAccessException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        List<Trip> trips = tripRepository.findAll();
        for (Trip trip : trips) {
            if (trip.getEndDate() != null && trip.getEndDate().toLocalDate().isBefore(java.time.LocalDate.now()) && Boolean.TRUE.equals(trip.getIsActive())) {
                trip.setIsActive(false);
                tripRepository.save(trip);
            }
        }
    }

    @Override
    public List<TripModel> addTripsRequestedByUser(String token, TripModel tripModel) throws UnauthorizedAccessException {
        TokenValidationResponse tokenValidationResponse = validateToken(token);
        if (tokenValidationResponse == null || !tokenValidationResponse.isValid()) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        String userName = tokenValidationResponse.getUsername();

        logger.info("Adding trips requested by user: {}", tripModel.title());
        List<TripRequest.RequestedItinerary> requestedItineraries = new ArrayList<>();
        if (tripModel.itineraries() != null) {
            for (ItineraryModel itinerary : tripModel.itineraries()) {
                requestedItineraries.add(RequestedItineraryMapper.toRequestedItinerary(ItineraryMapper.toEntity(itinerary)));
            }
        }
        TripRequest request = TripRequest.builder().title(tripModel.title()).description(tripModel.description()).startDate(tripModel.startDate()).endDate(tripModel.endDate()).price(tripModel.price()).mainDestinationId(tripModel.mainDestinationId()).requestedBy(userName).approved(false).itineraries(requestedItineraries).build();
        TripRequest savedRequest = tripRequestRepository.save(request);
        TripModel model = TripModel.builder().id(savedRequest.getId() != null ? Long.valueOf(savedRequest.getId()) : null).title(savedRequest.getTitle()).description(savedRequest.getDescription()).startDate(savedRequest.getStartDate()).endDate(savedRequest.getEndDate()).price(savedRequest.getPrice()).mainDestinationId(savedRequest.getMainDestinationId()).itineraries(tripModel.itineraries()).build();
        return List.of(model);
    }

    public TripModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws UnauthorizedAccessException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        TripRequest request = tripRequestRepository.findById(tripRequestId).orElseThrow(() -> new RuntimeException("TripRequest not found"));
        if (Boolean.TRUE.equals(request.getApproved())) {
            throw new RuntimeException("TripRequest already approved");
        }
        // Map the provided TripRequest (tripRequest param) to TripModel
        TripModel tripModel = TripModel.builder().title(tripRequest.getTitle()).description(tripRequest.getDescription()).startDate(tripRequest.getStartDate()).endDate(tripRequest.getEndDate()).price(tripRequest.getPrice()).mainDestinationId(tripRequest.getMainDestinationId()).createdBy(tripRequest.getRequestedBy()).itineraries(tripRequest.getItineraries().stream().map(itinerary -> ItineraryMapper.toModel(RequestedItineraryMapper.toItinerary(itinerary))).toList()).build();
        TripModel createdTrip = createTrip(token, tripModel);
        request.setApproved(true);
        logger.info("Deleting approved TripRequest: id={}, title={}, requestedBy={}", request.getId(), request.getTitle(), request.getRequestedBy());
        tripRequestRepository.delete(request);
        return createdTrip;
    }

    @Override
    public List<TripModel> getAllTripsRequested(String token) throws UnauthorizedAccessException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        List<TripRequest> requests = tripRequestRepository.findAll();
        List<TripModel> models = new ArrayList<>();
        for (TripRequest req : requests) {
            TripModel model = TripModel.builder().id(req.getId() != null ? Long.valueOf(req.getId()) : null).title(req.getTitle()).description(req.getDescription()).startDate(req.getStartDate()).endDate(req.getEndDate()).price(req.getPrice()).mainDestinationId(req.getMainDestinationId()).createdBy(req.getRequestedBy()).itineraries(req.getItineraries().stream().map(itinerary -> ItineraryMapper.toModel(RequestedItineraryMapper.toItinerary(itinerary))).toList()).build();

            models.add(model);
        }
        return models;
    }

    @Override
    public void indexTrip(TripModel tripModel) {
        try {
            IndexRequest<TripModel> request = IndexRequest.of(i -> i.index(tripIndex).id(tripModel.id() != null ? tripModel.id().toString() : tripModel.title()).document(tripModel));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index trip in Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    public List<String> suggestTrips(String query) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(tripIndex).query(q -> q.fuzzy(f -> f.field("title").value(query).fuzziness("AUTO"))).size(10));
            SearchResponse<TripModel> response = elasticsearchClient.search(searchRequest, TripModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).map(TripModel::title).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest trips from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }
}
