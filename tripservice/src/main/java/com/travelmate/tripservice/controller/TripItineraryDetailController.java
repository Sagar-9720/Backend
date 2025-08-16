package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.TripItineraryDetailModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.TripItineraryDetailService;
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
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> create(@RequestHeader("Authorization") String authHeader, @RequestBody TripItineraryDetailModel model) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            TripItineraryDetailModel saved = service.create(token, model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail created", saved, "/api/trip-itinerary-details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody TripItineraryDetailModel model) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            TripItineraryDetailModel updated = service.update(token, id, model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail updated", updated, "/api/trip-itinerary-details/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details/" + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            service.delete(token, id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail deleted", null, "/api/trip-itinerary-details/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TripItineraryDetailModel>> getById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            TripItineraryDetailModel detail = service.getById(token, id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Trip itinerary detail fetched", detail, "/api/trip-itinerary-details/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, e.getMessage(), "/api/trip-itinerary-details/" + id));
        }
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TripItineraryDetailModel>>> getAll(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            List<TripItineraryDetailModel> details = service.getAll(token);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "All trip itinerary details fetched", details, "/api/trip-itinerary-details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip-itinerary-details"));
        }
    }
}
