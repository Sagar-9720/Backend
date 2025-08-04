package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.domain.Trip;
import com.travelmate.tripservice.exceptions.TripNotFoundException;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.serviceimpl.TripServiceImpl;
import com.travelmate.tripservice.model.TripModel;
import com.travelmate.tripservice.mapper.TripMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripServiceImpl tripServiceImpl;

    // CREATE
    @PostMapping
    public ResponseEntity<CustomResponseEntity<TripModel>> createTrip(@Valid @RequestBody TripModel tripModel) {
        TripModel createdTrip = tripServiceImpl.createTrip(tripModel);
        return ResponseEntity.ok(CustomResponseEntity.success(201, "Trip created successfully", createdTrip, "/api/trips"));
    }

    // READ - Get trip by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripModel>> getTripById(@PathVariable Long id) {
        Optional<TripModel> trip = tripServiceImpl.getTripById(id);
        return trip.map(t -> ResponseEntity.ok(CustomResponseEntity.success(200, "Trip found", t, "/api/trips/" + id)))
                .orElseThrow(() -> new TripNotFoundException(id));
    }

    // READ - Get all trips
    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getAllTrips() {
        List<TripModel> trips = tripServiceImpl.getAllTrips();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "All trips retrieved", trips, "/api/trips"));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripModel>> updateTrip(@PathVariable Long id, @Valid @RequestBody TripModel tripModel) {
        TripModel updatedTrip = tripServiceImpl.updateTrip(id, tripModel);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip updated successfully", updatedTrip, "/api/trips/" + id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteTrip(@PathVariable Long id) {
        tripServiceImpl.deleteTrip(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip deleted", null, "/api/trips/" + id));
    }

    // FILTER - Get trips by destination name
    @GetMapping("/by-destination")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getTripsByDestinationName(@RequestParam String name) {
        List<TripModel> trips = tripServiceImpl.getTripsByDestinationName(name);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips filtered by destination", trips, "/api/trips/by-destination"));
    }

    // FILTER - Get trips by price range
    @GetMapping("/price-range")
    public ResponseEntity<CustomResponseEntity<List<TripModel>>> getTripsByPriceRange(@RequestParam BigDecimal start, @RequestParam BigDecimal end) {
        List<TripModel> trips = tripServiceImpl.tripsBtwPriceRanges(start, end);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Trips filtered by price range", trips, "/api/trips/price-range"));
    }

    // AUTO DELETE
    @DeleteMapping("/auto-delete/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> autoDeleteTrip(@PathVariable Long id) {
        tripServiceImpl.autoDeleteTripByDate(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Auto-deleted trip (if expired)", null, "/api/trips/auto-delete/" + id));
    }
}
