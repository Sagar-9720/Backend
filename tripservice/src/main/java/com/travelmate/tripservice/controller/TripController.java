package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.model.TripLiteModel;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.serviceimpl.TripServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip/trips")
public class TripController {

    @Autowired
    private TripServiceImpl tripService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getAllTrips(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
        }
        String token = authHeader.substring(7);
        List<TripLiteModel> trips = tripService.getAllTrips(token);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips fetched", trips, "/api/trips"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripModel>> getTripById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, HttpServletRequest servletRequest) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", "/api/trips/" + id));
        }
        String token = authHeader.substring(7);
        TripModel trip = tripService.getTripById(token, id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip fetched", trip, servletRequest.getRequestURI() + id));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> createTrip(@RequestHeader("Authorization") String authHeader, @RequestBody TripModel tripModel, HttpServletRequest servletRequest) throws Exception {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", "/api/trips"));
            }
            String token = authHeader.substring(7);
            TripLiteModel trip = tripService.createTrip(token, tripModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip created", trip, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> updateTrip(@RequestHeader("Authorization") String authHeader, @RequestBody TripModel tripModel, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            TripLiteModel updated = tripService.updateTrip(token, tripModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip updated", updated, "/api/trips/" + tripModel.id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/" + tripModel.id()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> deleteTrip(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            TripLiteModel deleted = tripService.deleteTrip(token, id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip deleted", deleted, "/api/trips/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/" + id));
        }
    }

    @PostMapping("/request")
    public ResponseEntity<CustomResponseEntity<TripRequest>> addTripRequest(@RequestHeader("Authorization") String authHeader, @RequestBody TripRequest tripRequest, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            TripRequest result = tripService.addTripsRequestedByUser(token, tripRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip request submitted", result, "/api/trips/request"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/request"));
        }
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<CustomResponseEntity<TripLiteModel>> approveTripRequest(@RequestHeader("Authorization") String authHeader, @PathVariable String requestId, @RequestBody TripRequest tripRequest, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            TripLiteModel approved = tripService.approveTripRequest(token, requestId, tripRequest);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip request approved", approved, "/api/trips/approve/" + requestId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/approve/" + requestId));
        }
    }

    @GetMapping("/by-destination")
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getTripsByDestinationName(@RequestHeader("Authorization") String authHeader, @RequestParam("destination") String destinationName, HttpServletRequest servletRequest) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
        }
        String token = authHeader.substring(7);
        List<TripLiteModel> trips = tripService.getTripsByDestinationName(token, destinationName);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by destination fetched", trips, "/api/trips/by-destination?destinationName=" + destinationName));
    }

    @GetMapping("/by-price-range")
    public ResponseEntity<CustomResponseEntity<List<TripLiteModel>>> getTripsByPriceRange(@RequestHeader("Authorization") String authHeader, @RequestParam BigDecimal startPrice, @RequestParam BigDecimal endPrice, HttpServletRequest servletRequest) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
        }
        String token = authHeader.substring(7);
        List<TripLiteModel> trips = tripService.tripsBtwPriceRanges(token, startPrice, endPrice);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by price range fetched", trips, "/api/trips/by-price-range?startPrice=" + startPrice + "&endPrice=" + endPrice));
    }

    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<CustomResponseEntity<List<TripRequest>>> getTripRequestsByUser(@RequestHeader("Authorization") String authHeader, @PathVariable String userId, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            List<TripRequest> requests = tripService.getTripRequestByUserId(token, userId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip requests for user fetched", requests, "/api/trips/requests/user/" + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), "/api/trips/requests/user/" + userId));
        }
    }

    @PostMapping("/auto-delete")
    public ResponseEntity<CustomResponseEntity<Void>> autoDeleteTripByDate(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            tripService.autoDeleteTripByDate(token);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips auto-deleted by date", null, "/api/trips/auto-delete"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), "/api/trips/auto-delete"));
        }
    }

    @GetMapping("/requests/all")
    public ResponseEntity<CustomResponseEntity<List<TripRequest>>> getAllTripsRequested(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            List<TripRequest> requests = tripService.getAllTripsRequested(token);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "All trip requests fetched", requests, "/api/trips/requests/all"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), "/api/trips/requests/all"));
        }
    }

    @GetMapping("/suggest")
    public ResponseEntity<CustomResponseEntity<List<String>>> suggestTrips(@RequestParam("q") String query) {
        List<String> suggestions = tripService.suggestTrips(query);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip suggestions fetched", suggestions, "/api/trip/trips/suggest?q=" + query));
    }

    @GetMapping("/get-trip-name")
    public ResponseEntity<CustomResponseEntity<String>> getTripsName(@RequestHeader("Authorization") String authHeader, @RequestBody List<String> tripIds, HttpServletRequest servletRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", "/api/trip/trips/get-trip-name"));
            }
            String token = authHeader.substring(7);
            List<Map<String, String>> tripNames = tripService.getTripNamesById(token, tripIds);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip name fetched", tripNames.toString(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), servletRequest.getRequestURI()));
        }
    }
}
