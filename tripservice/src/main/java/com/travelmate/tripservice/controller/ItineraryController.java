package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.dto.UserInfo;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ExtractHeader;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trip/itineraries")
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getAllItineraries(@RequestHeader("X-UserInfo") String authHeader) {
        try {
            List<ItineraryModel> itineraries = itineraryService.getAllItineraries();
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched", itineraries, "/api/trip/itineraries"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> getItineraryById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id) {
        try {
            ItineraryModel itinerary = itineraryService.getItineraryById(id);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary fetched", itinerary, "/api/trip/itineraries/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/" + id));
        }
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> createItinerary(@RequestHeader("X-UserInfo") String authHeader, @RequestBody ItineraryModel itineraryModel) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            ItineraryModel created = itineraryService.createItinerary(userInfo.role(), itineraryModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(201, "Itinerary created", created, "/api/trip/itineraries"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(400, "Failed to create itinerary: " + e.getMessage(), "/api/trip/itineraries"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> updateItinerary(@RequestHeader("X-UserInfo") String authHeader, @RequestBody ItineraryModel itineraryModel) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            ItineraryModel updated = itineraryService.updateItinerary(userInfo.role(), itineraryModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary updated", updated, "/api/trip/itineraries/" + updated.id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/" + itineraryModel.id()));
        }
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> getItinerariesByDestinationId(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long destinationId) {
        try {
            List<ItineraryModel> itineraries = itineraryService.getItinerariesByDestinationId(destinationId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itineraries fetched by destination", itineraries, "/api/trip/itineraries/destination/" + destinationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, e.getMessage(), "/api/trip/itineraries/destination/" + destinationId));
        }
    }

    @GetMapping("/suggest")
    public ResponseEntity<CustomResponseEntity<List<ItineraryModel>>> suggestItineraries(@RequestHeader("X-UserInfo") String authHeader, @RequestParam String keyword, @RequestParam(required = false) Long destinationId) {
        try {
            List<ItineraryModel> suggestions = itineraryService.suggestItineraries(keyword, destinationId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary suggestions fetched", suggestions, "/api/trip/itineraries/suggest?keyword=" + keyword));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, e.getMessage(), "/api/trip/itineraries/suggest?keyword=" + keyword));
        }
    }
}
