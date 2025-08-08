package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/trip/itineraries")
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getAllItineraries() {
        List<ItineraryModel> itineraries = itineraryService.getAllItineraries();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched", itineraries, "/itineraries"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> getItineraryById(@PathVariable Long id) {
        Optional<ItineraryModel> itinerary = itineraryService.getItineraryById(id);
        if (itinerary.isPresent()) {
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary fetched", itinerary.get(), "/itineraries/" + id));
        } else {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Itinerary not found", "/itineraries/" + id));
        }
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> createItinerary(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ItineraryModel itineraryModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel created = itineraryService.createItinerary(token, itineraryModel);
            return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Itinerary created", created, "/itineraries"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to create itinerary: " + e.getMessage(), "/itineraries"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> updateItinerary(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ItineraryModel itineraryModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel updated = itineraryService.updateItinerary(token, itineraryModel);
            if (updated != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary updated", updated, "/itineraries/" + itineraryModel.getId()));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Itinerary not found", "/itineraries/" + itineraryModel.getId()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to update itinerary: " + e.getMessage(), "/itineraries/" + itineraryModel.getId()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> deleteItinerary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel deleted = itineraryService.deleteItinerary(token, id);
            if (deleted != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary deleted", deleted, "/itineraries/" + id));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Itinerary not found", "/itineraries/" + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to delete itinerary: " + e.getMessage(), "/itineraries/" + id));
        }
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getItinerariesByDestinationId(@PathVariable Long destinationId) {
        List<ItineraryModel> itineraries = itineraryService.getItinerariesByDestinationId(destinationId);
        if (itineraries == null || itineraries.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No itineraries found for destination", "/itineraries/destination/" + destinationId));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched by destination", itineraries, "/itineraries/destination/" + destinationId));
    }
}
