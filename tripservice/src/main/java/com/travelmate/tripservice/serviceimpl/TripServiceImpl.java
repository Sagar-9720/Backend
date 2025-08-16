package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.TokenValidationResponse;
import com.travelmate.tripservice.entity.*;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.*;
import com.travelmate.tripservice.model.*;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.repository.TripRequestRepository;
import com.travelmate.tripservice.service.*;
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
    private DestinationServiceImpl destinationService;
    @Autowired
    private ItineraryServiceImpl itineraryService;

    @Autowired
    private TripItineraryDetailService itineraryDetailService;

    @Autowired
    private ItineraryActivityServiceImpl itineraryActivityService;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Value("${elasticsearch.index.trips:trips}")
    private String tripIndex;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);
    @Autowired
    private TripItineraryDetailService tripItineraryDetailService;


    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripLiteModel createTrip(String token, TripModel tripModel) throws TripExistsException, UnauthorizedAccessException, JsonProcessingException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        String userName = tokenValidationService.getUserName(token);
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        logger.info("Creating trip: {}", tripModel.title());
        List<Trip> existing = tripRepository.findByTitleContainingIgnoreCase(tripModel.title());
        if (!existing.isEmpty()) {
            throw new TripExistsException(tripModel.title());
        }
        Trip trip = TripMapper.toEntity(tripModel);
        if (tripModel.destination().id() == null) {
            trip.setMainDestination(DestinationMapper.toEntity(destinationService.createDestination(token, tripModel.destination())));
        }
        if (tripModel.itineraryDetails() != null && !tripModel.itineraryDetails().isEmpty()) {
            List<TripItineraryDetail> itineraries = new ArrayList<>();
            for (TripItineraryDetailModel itinerary : tripModel.itineraryDetails()) {
                TripItineraryDetail tripItineraryDetail = TripItineraryDetailMapper.toEntity(itinerary);
                itineraries.add(TripItineraryDetailMapper.toEntity(tripItineraryDetailService.create(token, TripItineraryDetailMapper.toModel(tripItineraryDetail))));
            }
            trip.setTripItineraryDetails(itineraries);
        }
        trip.setCreatedBy(userName);
        trip.setIsActive(true);
        Trip savedTrip = tripRepository.save(trip);
        indexTrip(TripMapper.toModel(savedTrip));
        return TripMapper.toLiteModel(savedTrip);
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
    public List<TripLiteModel> getAllTrips(String token) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching all trips");
        return tripRepository.findAll().stream().map(TripMapper::toLiteModel).toList();
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripLiteModel updateTrip(String token, TripModel updatedTripModel) throws TripNotFoundException, UnauthorizedAccessException, JsonProcessingException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
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

        // Set main destination (create if needed)
        if (updatedTripModel.destination() != null) {
            if (updatedTripModel.destination().id() == null) {
                existingTrip.setMainDestination(DestinationMapper.toEntity(destinationService.createDestination(token, updatedTripModel.destination())));
            } else {
                DestinationModel destinationModel = destinationService.getDestinationById(token, updatedTripModel.destination().id());
                if (destinationModel == null) {
                    throw new DestinationNotFoundException(updatedTripModel.destination().id());
                }
                existingTrip.setMainDestination(DestinationMapper.toEntity(destinationModel));
            }
        }
        // Set itinerary details (create if needed)
        if (updatedTripModel.itineraryDetails() != null && !updatedTripModel.itineraryDetails().isEmpty()) {
            List<TripItineraryDetail> itineraryDetails = new ArrayList<>();
            for (TripItineraryDetailModel itinerary : updatedTripModel.itineraryDetails()) {
                TripItineraryDetail tripItineraryDetail = TripItineraryDetailMapper.toEntity(itinerary);
                if (itinerary.id() == null) {
                    itineraryDetails.add(TripItineraryDetailMapper.toEntity(tripItineraryDetailService.create(token, TripItineraryDetailMapper.toModel(tripItineraryDetail))));
                } else {
                    TripItineraryDetailModel existingItinerary = tripItineraryDetailService.getById(token, itinerary.id());
                    itineraryDetails.add(TripItineraryDetailMapper.toEntity(existingItinerary));
                }
            }
            existingTrip.setTripItineraryDetails(itineraryDetails);
        }
        Trip savedTrip = tripRepository.save(existingTrip);
        indexTrip(TripMapper.toModel(savedTrip)); // Re-index the updated trip in Elasticsearch
        return TripMapper.toLiteModel(savedTrip);
    }

    @Override
    @CacheEvict(value = {"trips", "tripsAll"}, allEntries = true)
    public TripLiteModel deleteTrip(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException, JsonProcessingException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        logger.info("Disabling trip id: {}", id);
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        trip.setIsActive(false);
        tripRepository.save(trip);
        return TripMapper.toLiteModel(trip);
    }


    @Override
    @Cacheable(value = "tripsByDestination", key = "#destinationName")
    public List<TripLiteModel> getTripsByDestinationName(String token, String destinationName) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trips by destination name: {}", destinationName);
        List<Trip> trips = tripRepository.findByMainDestinationContainingIgnoreCase(destinationName);
        return trips.stream()
                .map(TripMapper::toLiteModel)
                .toList();
    }

    @Override
    @Cacheable(value = "tripsByPrice", key = "#startPrice.toString().concat('-').concat(#endPrice.toString())")
    public List<TripLiteModel> tripsBtwPriceRanges(String token, BigDecimal startPrice, BigDecimal endPrice) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trips between price range: {} - {}", startPrice, endPrice);
        return tripRepository.findByPriceBetween(startPrice, endPrice).stream().map(TripMapper::toLiteModel).toList();
    }

    @Override
    public void autoDeleteTripByDate(String token) throws UnauthorizedAccessException, JsonProcessingException {
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

    public List<Map<String, String>> getTripNamesById(String token, List<String> tripIds) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        logger.info("Fetching trip names by IDs: {}", tripIds);
        List<Map<String, String>> tripNames = new ArrayList<>();
        for (String id : tripIds) {
            Optional<Trip> tripOpt = tripRepository.findById(Long.valueOf(id));
            if (tripOpt.isPresent()) {
                Trip trip = tripOpt.get();
                Map<String, String> tripNameMap = new HashMap<>();
                tripNameMap.put("id", String.valueOf(trip.getId()));
                tripNameMap.put("title", trip.getTitle());
                tripNames.add(tripNameMap);
            }
        }
        return tripNames;
    }


    // User Trip Request Thing

    @Override
    public List<TripRequest> getTripRequestByUserId(String token, String userId) throws UnauthorizedAccessException, JsonProcessingException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        String role = tokenValidationService.getRole(token);
        String authUserId = tokenValidationService.getUserId(token);
        if (!authUserId.equals(userId) && !"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not authorized to view this trip request");
        }
        logger.info("Fetching trip requests for user: {}", userId);
        List<TripRequest> requests = tripRequestRepository.findByRequestedBy(userId);
        if (requests.isEmpty()) {
            logger.warn("No trip requests found for user: {}", userId);
            return Collections.emptyList();
        } else {
            logger.info("Found {} trip requests for user: {}", requests.size(), userId);
            return requests;
        }
    }

    @Override
    public TripLiteModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws UnauthorizedAccessException, JsonProcessingException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        TripRequest request = tripRequestRepository.findById(tripRequestId).orElseThrow(() -> new RuntimeException("TripRequest not found"));
        if (Boolean.TRUE.equals(request.getApproved())) {
            throw new RuntimeException("TripRequest already approved");
        }
        Trip tripEntity = RequestedTripMapper.toTripEntity(request);
        Trip savedTrip = tripRepository.save(tripEntity);
        request.setApproved(true);
        logger.info("Deleting approved TripRequest: id={}, title={}, requestedBy={}", request.getId(), request.getTitle(), request.getRequestedBy());
        tripRequestRepository.delete(request);
        return TripMapper.toLiteModel(savedTrip);
    }

    @Override
    public List<TripRequest> getAllTripsRequested(String token) throws UnauthorizedAccessException, JsonProcessingException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }
        List<TripRequest> requests = tripRequestRepository.findAll();
        if (requests.isEmpty()) {
            logger.warn("No trip requests found");
            return Collections.emptyList();
        } else {
            logger.info("Found {} trip requests", requests.size());
            return requests;
        }
    }

    @Override
    public TripRequest addTripsRequestedByUser(String token, TripRequest tripRequest) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        return tripRequestRepository.save(tripRequest);

    }


}
