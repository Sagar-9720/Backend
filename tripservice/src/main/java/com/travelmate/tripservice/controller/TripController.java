package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/trip/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getAllTrips() {
        List<TripModel> trips = tripService.getAllTrips();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips fetched", trips, "/api/trips"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripModel>> getTripById(@PathVariable Long id) throws Exception {
        return tripService.getTripById(id)
                .map(trip -> ResponseEntity.ok(CustomResponseEntity.success(200, "Trip fetched", trip, "/api/trips/" + id)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, "Trip not found", "/api/trips/" + id)));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TripModel>> createTrip(@RequestHeader("Authorization") String token, @RequestBody TripModel tripModel) {
        try {
            TripModel created = tripService.createTrip(token, tripModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip created", created, "/api/trips"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripModel>> updateTrip(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody TripModel tripModel) {
        try {
            tripModel.setId(id);
            TripModel updated = tripService.updateTrip(token, tripModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip updated", updated, "/api/trips/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/" + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteTrip(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            tripService.deleteTrip(token, id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip deleted", null, "/api/trips/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/" + id));
        }
    }

    @PostMapping("/request")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> addTripRequest(@RequestHeader("Authorization") String token, @RequestBody TripModel tripModel) {
        try {
            List<TripModel> result = tripService.addTripsRequestedByUser(token, tripModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Trip request submitted", result, "/api/trips/request"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/request"));
        }
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<CustomResponseEntity<TripModel>> approveTripRequest(@RequestHeader("Authorization") String token, @PathVariable String requestId, @RequestBody TripRequest tripRequest) {
        try {
            TripModel approved = tripService.approveTripRequest(token, requestId, tripRequest);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip request approved", approved, "/api/trips/approve/" + requestId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trips/approve/" + requestId));
        }
    }

    @GetMapping("/by-destination")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getTripsByDestinationName(@RequestParam String destinationName) {
        List<TripModel> trips = tripService.getTripsByDestinationName(destinationName);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by destination fetched", trips, "/api/trips/by-destination?destinationName=" + destinationName));
    }

    @GetMapping("/by-price-range")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getTripsByPriceRange(@RequestParam BigDecimal startPrice, @RequestParam BigDecimal endPrice) {
        List<TripModel> trips = tripService.tripsBtwPriceRanges(startPrice, endPrice);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips by price range fetched", trips, "/api/trips/by-price-range?startPrice=" + startPrice + "&endPrice=" + endPrice));
    }

    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getTripRequestsByUser(@PathVariable String userId) {
        try {
            List<TripModel> requests = tripService.getTripRequestByUserId(userId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip requests for user fetched", requests, "/api/trips/requests/user/" + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), "/api/trips/requests/user/" + userId));
        }
    }

    @PostMapping("/auto-delete")
    public ResponseEntity<CustomResponseEntity<Void>> autoDeleteTripByDate() {
        try {
            tripService.autoDeleteTripByDate();
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips auto-deleted by date", null, "/api/trips/auto-delete"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(500, e.getMessage(), "/api/trips/auto-delete"));
        }
    }
}
