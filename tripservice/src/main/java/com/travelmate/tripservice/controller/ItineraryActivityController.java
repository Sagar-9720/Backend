package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.ItineraryActivityModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ItineraryActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip/itinerary-activities")
public class ItineraryActivityController {
    @Autowired
    private ItineraryActivityService service;

    @PostMapping
    public ResponseEntity<CustomResponseEntity<ItineraryActivityModel>> create(@RequestHeader("X-UserInfo") String authHeader, @RequestBody ItineraryActivityModel model) {
        try {
            ItineraryActivityModel saved = service.create(model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary activity created", saved, "/api/itinerary-activities"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/itinerary-activities"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryActivityModel>> update(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id, @RequestBody ItineraryActivityModel model) {
        try {
            ItineraryActivityModel updated = service.update(id, model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary activity updated", updated, "/api/itinerary-activities/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/itinerary-activities/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryActivityModel>> getById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id) {
        try {
            ItineraryActivityModel activity = service.getById(id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary activity fetched", activity, "/api/itinerary-activities/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, e.getMessage(), "/api/itinerary-activities/" + id));
        }
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<ItineraryActivityModel>>> getAll(@RequestHeader("X-UserInfo") String authHeader) {
        try {
            List<ItineraryActivityModel> activities = service.getAll();
            return ResponseEntity.ok(CustomResponseEntity.success(200, "All itinerary activities fetched", activities, "/api/itinerary-activities"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/itinerary-activities"));
        }
    }

    @GetMapping("/suggest")
    public ResponseEntity<CustomResponseEntity<List<ItineraryActivityModel>>> suggest(@RequestHeader("X-UserInfo") String authHeader, @RequestParam String keyword) {
        try {
            List<ItineraryActivityModel> suggestions = service.suggest(keyword);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary activity suggestions fetched", suggestions, "/api/itinerary-activities/suggest?keyword=" + keyword));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/itinerary-activities/suggest?keyword=" + keyword));
        }
    }
}
