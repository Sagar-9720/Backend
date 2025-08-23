package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.dto.UserInfo;
import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.model.TripLiteModel;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ExtractHeader;
import com.travelmate.tripservice.serviceimpl.TripServiceImpl;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip/trips")
@Timed(value = "trip.controller", description = "Trip controller timing metrics")
public class TripController {

    @Autowired
    private TripServiceImpl tripService;
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    @GetMapping
    @Timed(value = "trip.getAllTrips", description = "Time taken to fetch all trips", longTask = true)
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getAllTrips(@RequestHeader("X-UserInfo") String authHeader, HttpServletRequest servletRequest) throws Exception {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            List<TripLiteModel> trips = tripService.getAllTrips(userInfo.role());
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips fetched", trips, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/{id}")
    @Timed(value = "trip.getTripById", description = "Time taken to fetch trip by id")
    public ResponseEntity<CustomResponseEntity<TripModel>> getTripById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id, HttpServletRequest servletRequest) {
        try {
            logger.info("Fetching trip with ID: {}", id);
            TripModel trip = tripService.getTripById(id);
            logger.info("Successfully fetched trip: {}", trip.title());
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip fetched successfully", trip, servletRequest.getRequestURI() + id));
        } catch (Exception e) {
            logger.error("Error fetching trip by id: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, "Error fetching trip: " + e.getMessage(), servletRequest.getRequestURI() + id));
        }
    }

    @PostMapping
    @Timed(value = "trip.createTrip", description = "Time taken to create a trip", longTask = true)
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> createTrip(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TripModel tripModel, HttpServletRequest servletRequest) throws Exception {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            TripLiteModel trip = tripService.createTrip(userInfo.username(), userInfo.role(), tripModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip created", trip, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PutMapping
    @Timed(value = "trip.updateTrip", description = "Time taken to update a trip", longTask = true)
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> updateTrip(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TripModel tripModel, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            TripLiteModel updated = tripService.updateTrip(userInfo.role(), tripModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip updated", updated, servletRequest.getRequestURI() + tripModel.id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), servletRequest.getRequestURI() + tripModel.id()));
        }
    }

    @DeleteMapping("/{id}")
    @Timed(value = "trip.deleteTrip", description = "Time taken to delete a trip")
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> deleteTrip(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            TripLiteModel deleted = tripService.deleteTrip(userInfo.role(), id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip deleted", deleted, servletRequest.getRequestURI() + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), servletRequest.getRequestURI() + id));
        }
    }

    @PostMapping("/request")
    @Timed(value = "trip.addTripRequest", description = "Time taken to add a trip request")
    public ResponseEntity<CustomResponseEntity<TripRequest>> addTripRequest(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TripRequest tripRequest, HttpServletRequest servletRequest) {
        try {
            TripRequest result = tripService.addTripsRequestedByUser(tripRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip request submitted", result, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/approve/{requestId}")
    @Timed(value = "trip.approveTripRequest", description = "Time taken to approve a trip request")
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> approveTripRequest(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String requestId, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            TripLiteModel approved = tripService.approveTripRequest(userInfo.role(), requestId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip request approved", approved, servletRequest.getRequestURI() + requestId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), servletRequest.getRequestURI() + requestId));
        }
    }

    @GetMapping("/by-destination")
    @Timed(value = "trip.getTripsByDestinationName", description = "Time taken to get trips by destination name")
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getTripsByDestinationName(@RequestHeader("X-UserInfo") String authHeader, @RequestParam("destination") String destinationName, HttpServletRequest servletRequest) throws Exception {
        UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
        List<TripLiteModel> trips = tripService.getTripsByDestinationName(userInfo.role(), destinationName);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by destination fetched", trips, servletRequest.getRequestURI() + destinationName));
    }

    @GetMapping("/by-price-range")
    @Timed(value = "trip.getTripsByPriceRange", description = "Time taken to get trips by price range")
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getTripsByPriceRange(@RequestHeader("X-UserInfo") String authHeader, @RequestParam BigDecimal startPrice, @RequestParam BigDecimal endPrice, HttpServletRequest servletRequest) throws Exception {

        UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
        List<TripLiteModel> trips = tripService.tripsBtwPriceRanges(userInfo.role(), startPrice, endPrice);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by price range fetched", trips, servletRequest.getRequestURI()));
    }

    @GetMapping("/requests/user/{userId}")
    @Timed(value = "trip.getTripRequestsByUser", description = "Time taken to get trip requests by user")
    public ResponseEntity<CustomResponseEntity<List<TripRequest>>> getTripRequestsByUser(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String userId, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            List<TripRequest> requests = tripService.getTripRequestByUserId(userInfo.userId(), userInfo.role(), userId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip requests for user fetched", requests, servletRequest.getRequestURI() + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI() + userId));
        }
    }

    @PostMapping("/auto-delete")
    @Timed(value = "trip.autoDeleteTripByDate", description = "Time taken to auto delete trips by date")
    public ResponseEntity<CustomResponseEntity<Void>> autoDeleteTripByDate(@RequestHeader("X-UserInfo") String authHeader, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            tripService.autoDeleteTripByDate(userInfo.role());
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips auto-deleted by date", null, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/requests/all")
    @Timed(value = "trip.getAllTripsRequested", description = "Time taken to get all trip requests")
    public ResponseEntity<CustomResponseEntity<List<TripRequest>>> getAllTripsRequested(@RequestHeader("X-UserInfo") String authHeader, HttpServletRequest servletRequest) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            List<TripRequest> requests = tripService.getAllTripsRequested(userInfo.role());
            return ResponseEntity.ok(CustomResponseEntity.success(200, "All trip requests fetched", requests, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/suggest")
    @Timed(value = "trip.suggestTrips", description = "Time taken to suggest trips")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> suggestTrips(@RequestParam("q") String query) {
        List<TripModel> suggestions = tripService.suggestTrips(query);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip suggestions fetched", suggestions, "/api/trip/trips/suggest?q=" + query));
    }

    @GetMapping("/get-trip-name")
    @Timed(value = "trip.getTripsName", description = "Time taken to get trip names")
    public ResponseEntity<CustomResponseEntity<String>> getTripsName(@RequestHeader("X-UserInfo") String authHeader, @RequestBody List<String> tripIds, HttpServletRequest servletRequest) {
        try {
            List<Map<String, String>> tripNames = tripService.getTripNamesById(tripIds);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip name fetched", tripNames.toString(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI()));
        }
    }
}
