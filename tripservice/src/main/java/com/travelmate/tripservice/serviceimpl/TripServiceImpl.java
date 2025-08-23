package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.entity.*;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.*;
import com.travelmate.tripservice.model.*;
import com.travelmate.tripservice.repository.*;
import com.travelmate.tripservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DestinationServiceImpl destinationService;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.trips:trips}")
    private String tripIndex;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @Autowired
    private TripItineraryDetailService tripItineraryDetailService;

    @Autowired
    private ItineraryActivityRepository activityRepository;

    @Override
    @CacheEvict(value = {"trips", "allTrips", "tripsByDestination", "tripsByPrice", "tripSuggestions", "tripNames"}, allEntries = true)
    @Transactional
    public TripLiteModel createTrip(String userName, String role, TripModel tripModel) throws TripExistsException, UnauthorizedAccessException {

        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }

        logger.info("Creating trip: {}", tripModel.title());

        List<Trip> existing = tripRepository.findByTitleContainingIgnoreCase(tripModel.title());
        if (!existing.isEmpty()) {
            throw new TripExistsException(tripModel.title());
        }

        Trip trip = TripMapper.toEntity(tripModel);
        trip.setCreatedBy(userName);
        trip.setIsActive(true);

        // Handle main destination
        if (tripModel.destination() != null) {
            if (tripModel.destination().id() == null) {
                trip.setMainDestination(DestinationMapper.toEntity(destinationService.createDestination(role, tripModel.destination())));
            } else {
                trip.setMainDestination(DestinationMapper.toEntity(destinationService.getDestinationById(tripModel.destination().id())));
            }
        }

        // Handle itinerary details with activities
        if (tripModel.itineraryDetails() != null && !tripModel.itineraryDetails().isEmpty()) {
            Set<TripItineraryDetail> itineraries = tripModel.itineraryDetails().stream().map(itineraryModel -> {
                TripItineraryDetail detail = TripItineraryDetailMapper.toEntity(itineraryModel);

                // Map activities
                if (itineraryModel.activities() != null) {
                    Set<ItineraryActivity> activities = itineraryModel.activities().stream().map(act -> activityRepository.findById(act.id()).orElseThrow(() -> new RuntimeException("Activity not found: " + act.id()))).collect(Collectors.toSet());
                    detail.setActivities(activities);
                }
                return detail;
            }).collect(Collectors.toSet());

            trip.setTripItineraryDetails(itineraries);
        }

        Trip savedTrip = tripRepository.save(trip);
        indexTrip(TripMapper.toModel(savedTrip));

        return TripMapper.toLiteModel(savedTrip);
    }

    @Override
    @Cacheable(value = "trips", key = "#id")
    public TripModel getTripById(Long id) throws TripNotFoundException {
        logger.info("Fetching trip by id: {}", id);
        return tripRepository.findById(id).map(TripMapper::toModel).orElseThrow(() -> new TripNotFoundException(id));
    }

    @Override
    @Cacheable(value = "allTrips", key = "#role.toLowerCase()")
    public List<TripLiteModel> getAllTrips(String role) {
        logger.info("Cache miss: Fetching all trips from database for role: {}", role);
        List<TripLiteModel> list = tripRepository.findAll().stream().map(TripMapper::toLiteModel).collect(Collectors.toList());

        if ("user".equalsIgnoreCase(role) || "guest".equalsIgnoreCase(role)) {
            list = list.stream().filter(TripLiteModel::isActive).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    @CacheEvict(value = {"trips", "allTrips", "tripsByDestination", "tripsByPrice", "tripSuggestions", "tripNames"}, allEntries = true)
    @Transactional
    public TripLiteModel updateTrip(String role, TripModel updatedTripModel) throws TripNotFoundException, UnauthorizedAccessException {

        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }

        Trip existingTrip = tripRepository.findById(updatedTripModel.id()).orElseThrow(() -> new TripNotFoundException(updatedTripModel.id()));

        existingTrip.setTitle(updatedTripModel.title());
        existingTrip.setDescription(updatedTripModel.description());
        existingTrip.setStartDate(updatedTripModel.startDate());
        existingTrip.setEndDate(updatedTripModel.endDate());
        existingTrip.setPrice(updatedTripModel.price());

        // Update main destination
        if (updatedTripModel.destination() != null) {
            if (updatedTripModel.destination().id() == null) {
                existingTrip.setMainDestination(DestinationMapper.toEntity(destinationService.createDestination(role, updatedTripModel.destination())));
            } else {
                existingTrip.setMainDestination(DestinationMapper.toEntity(destinationService.getDestinationById(updatedTripModel.destination().id())));
            }
        }

        // Update itinerary details
        if (updatedTripModel.itineraryDetails() != null && !updatedTripModel.itineraryDetails().isEmpty()) {
            Set<TripItineraryDetail> itineraryDetails = updatedTripModel.itineraryDetails().stream().map(itineraryModel -> {
                TripItineraryDetail detail;
                if (itineraryModel.id() != null) {
                    TripItineraryDetailModel existingDetail = tripItineraryDetailService.getById(itineraryModel.id());
                    detail = TripItineraryDetailMapper.toEntity(existingDetail);
                } else {
                    detail = TripItineraryDetailMapper.toEntity(itineraryModel);
                }

                // Map activities
                if (itineraryModel.activities() != null) {
                    Set<ItineraryActivity> activities = itineraryModel.activities().stream().map(act -> activityRepository.findById(act.id()).orElseThrow(() -> new RuntimeException("Activity not found: " + act.id()))).collect(Collectors.toSet());
                    detail.setActivities(activities);
                }
                return detail;
            }).collect(Collectors.toSet());

            existingTrip.setTripItineraryDetails(itineraryDetails);
        }

        Trip savedTrip = tripRepository.save(existingTrip);
        indexTrip(TripMapper.toModel(savedTrip));
        return TripMapper.toLiteModel(savedTrip);
    }

    @Override
    @CacheEvict(value = {"trips", "allTrips", "tripsByDestination", "tripsByPrice", "tripSuggestions", "tripNames"}, allEntries = true)
    public TripLiteModel deleteTrip(String role, Long id) throws TripNotFoundException, UnauthorizedAccessException {
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User is not ADMIN or SUBADMIN");
        }

        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        trip.setIsActive(false);
        tripRepository.save(trip);

        return TripMapper.toLiteModel(trip);
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
    @Cacheable(value = "tripSuggestions", key = "#query")
    public List<TripModel> suggestTrips(String query) {
        logger.info("Fetching trip suggestions for query: {}", query);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(tripIndex).query(q -> q.bool(b -> b.should(sh -> sh.prefix(p -> p.field("title.keyword").value(query.toLowerCase()))).should(sh -> sh.wildcard(w -> w.field("title").value("*" + query.toLowerCase() + "*"))).minimumShouldMatch("1"))).size(10));

            SearchResponse<TripModel> response = elasticsearchClient.search(searchRequest, TripModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(Objects::nonNull).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest trips from Elasticsearch: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    @Override
    @Cacheable(value = "tripsByDestination", key = "#destinationName")
    public List<TripLiteModel> getTripsByDestinationName(String role, String destinationName) throws DestinationNotFoundException {
        logger.info("Fetching trips by destination name: {}", destinationName);
        List<Trip> trips = tripRepository.findByMainDestinationContainingIgnoreCase(destinationName);
        if (role.equalsIgnoreCase("user") || role.equalsIgnoreCase("guest")) {
            trips = trips.stream().filter(Trip::getIsActive).toList();
        }
        return trips.stream().map(TripMapper::toLiteModel).toList();
    }

    @Override
    @Cacheable(value = "tripsByPrice", key = "#startPrice.toString().concat('-').concat(#endPrice.toString())")
    public List<TripLiteModel> tripsBtwPriceRanges(String role, BigDecimal startPrice, BigDecimal endPrice) throws UnauthorizedAccessException {
        logger.info("Fetching trips between price range: {} - {}", startPrice, endPrice);
        List<TripLiteModel> trips = tripRepository.findByPriceBetween(startPrice, endPrice).stream().map(TripMapper::toLiteModel).toList();
        if (role.equalsIgnoreCase("user") || role.equalsIgnoreCase("guest")) {
            trips = trips.stream().filter(TripLiteModel::isActive).collect(Collectors.toList());
        }
        return trips;
    }

    @Override
    public void autoDeleteTripByDate(String role) throws UnauthorizedAccessException {
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
    @Cacheable(value = "tripNames", key = "#tripIds.hashCode()")
    public List<Map<String, String>> getTripNamesById(List<String> tripIds) {
        logger.info("Cache miss: Fetching trip names by IDs: {}", tripIds);
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
    public List<TripRequest> getTripRequestByUserId(String authUserId, String role, String userId) throws UnauthorizedAccessException {
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
    public TripLiteModel approveTripRequest(String role, String tripRequestId) throws UnauthorizedAccessException {

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
    public List<TripRequest> getAllTripsRequested(String role) throws UnauthorizedAccessException {

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
    public TripRequest addTripsRequestedByUser(TripRequest tripRequest) {
        return tripRequestRepository.save(tripRequest);
    }


}
