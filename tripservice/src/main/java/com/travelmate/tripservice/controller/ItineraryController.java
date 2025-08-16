package com.travelmate.tripservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trip/itineraries")
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getAllItineraries(@RequestHeader("Authorization") String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            List<ItineraryModel> itineraries = itineraryService.getAllItineraries(token);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched", itineraries, "/api/trip/itineraries"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> getItineraryById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            Optional<ItineraryModel> itinerary = itineraryService.getItineraryById(token, id);
            if (itinerary.isPresent()) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary fetched", itinerary.get(), "/api/trip/itineraries/" + id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, "Itinerary not found", "/api/trip/itineraries/" + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/" + id));
        }
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> createItinerary(@RequestHeader("Authorization") String authHeader, @RequestBody ItineraryModel itineraryModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel created = itineraryService.createItinerary(token, itineraryModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Itinerary created", created, "/api/trip/itineraries"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(400, "Failed to create itinerary: " + e.getMessage(), "/api/trip/itineraries"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> updateItinerary(@RequestHeader("Authorization") String authHeader, @RequestBody ItineraryModel itineraryModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel updated = itineraryService.updateItinerary(token, itineraryModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary updated", updated, "/api/trip/itineraries/" + updated.id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/" + itineraryModel.id()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> deleteItinerary(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            ItineraryModel deleted = itineraryService.deleteItinerary(token, id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary deleted", deleted, "/api/trip/itineraries/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/" + id));
        }
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getItinerariesByDestinationId(@RequestHeader("Authorization") String authHeader, @PathVariable Long destinationId) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            List<ItineraryModel> itineraries = itineraryService.getItinerariesByDestinationId(token, destinationId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched by destination", itineraries, "/api/trip/itineraries/destination/" + destinationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, e.getMessage(), "/api/trip/itineraries/destination/" + destinationId));
        }
    }

    @GetMapping("/suggest")
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> suggestItineraries(@RequestHeader("Authorization") String authHeader, @RequestParam String keyword, @RequestParam(required = false) Long destinationId) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            List<ItineraryModel> suggestions = itineraryService.suggestItineraries(token, keyword, destinationId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary suggestions fetched", suggestions, "/api/trip/itineraries/suggest?keyword=" + keyword));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/suggest?keyword=" + keyword));
        }
    }
}
