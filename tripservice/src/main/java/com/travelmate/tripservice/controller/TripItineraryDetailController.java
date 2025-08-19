package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.TripItineraryDetailModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.serviceimpl.TripItineraryDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip/trip-itinerary-details")
public class TripItineraryDetailController {

    @Autowired
    private TripItineraryDetailServiceImpl service;

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> create(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TripItineraryDetailModel model) {
        try {
            TripItineraryDetailModel saved = service.create(model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail created", saved, "/api/trip-itinerary-details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> update(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id, @RequestBody TripItineraryDetailModel model) {
        try {
            TripItineraryDetailModel updated = service.update(id, model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail updated", updated, "/api/trip-itinerary-details/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> getById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id) {
        try {
            TripItineraryDetailModel detail = service.getById(id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail fetched", detail, "/api/trip-itinerary-details/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, e.getMessage(), "/api/trip-itinerary-details/" + id));
        }
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TripItineraryDetailModel>>> getAll(@RequestHeader("X-UserInfo") String authHeader) {
        try {
            List<TripItineraryDetailModel> details = service.getAll();
            return ResponseEntity.ok(CustomResponseEntity.success(200, "All trip itinerary details fetched", details, "/api/trip-itinerary-details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details"));
        }
    }
}
