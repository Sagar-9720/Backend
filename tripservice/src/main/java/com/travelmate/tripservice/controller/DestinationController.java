package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.dto.UserInfo;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.ExtractHeader;
import com.travelmate.tripservice.serviceimpl.DestinationServiceImpl;
import io.micrometer.core.annotation.Timed;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip/destinations")
@Timed(value = "destination.controller", description = "Destination controller timing metrics")
public class DestinationController {

    private static final Logger logger = LoggerFactory.getLogger(DestinationController.class);

    @Autowired
    private DestinationServiceImpl destinationServiceImpl;

    @PostMapping
    @Timed(value = "destination.create", description = "Time taken to create a destination")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> create(@RequestHeader("X-UserInfo") String authHeader, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            DestinationModel saved = destinationServiceImpl.createDestination(userInfo.role(), destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination created", saved, "/api/destinations"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, e.getMessage(), "/api/destinations"));
        }
    }

    @PutMapping
    @Timed(value = "destination.update", description = "Time taken to update a destination")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> update(@RequestHeader("X-UserInfo") String authHeader, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            UserInfo userInfo = ExtractHeader.extractHeader(authHeader);
            DestinationModel updated = destinationServiceImpl.updateDestination(userInfo.role(), destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination updated", updated, "/api/destinations/" + updated.id()));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(CustomResponseEntity.error(403, e.getMessage(), "/api/destinations/"));
        }
    }


    @GetMapping("/{id}")
    @Timed(value = "destination.getById", description = "Time taken to get destination by ID")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> getById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long id) {
        DestinationModel destination = destinationServiceImpl.getDestinationById(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination fetched", destination, "/api/destinations/" + id));
    }

    @GetMapping
    @Timed(value = "destination.getAll", description = "Time taken to get all destinations")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getAll(@RequestHeader("X-UserInfo") String authHeader) {
        List<DestinationModel> destinations = destinationServiceImpl.getAllDestinations();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "All destinations fetched", destinations, "/api/destinations"));
    }

    @GetMapping("/region/{regionId}")
    @Timed(value = "destination.getByRegion", description = "Time taken to get destinations by region")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByRegion(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long regionId) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByRegionId(regionId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by region fetched", destinations, "/api/destinations/region/" + regionId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/region/" + regionId));
        }
    }

    @GetMapping("/country/{countryId}")
    @Timed(value = "destination.getByCountry", description = "Time taken to get destinations by country")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByCountry(@RequestHeader("X-UserInfo") String authHeader, @PathVariable Long countryId) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByCountryId(countryId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by country fetched", destinations, "/api/destinations/country/" + countryId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/country/" + countryId));
        }
    }

    @GetMapping("/search")
    @Timed(value = "destination.searchByName", description = "Time taken to search destinations by name")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> searchByName(@RequestHeader("X-UserInfo") String authHeader, @RequestParam String name) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.searchDestinationByName(name);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations searched by name", destinations, "/api/destinations/search?name=" + name));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/search?name=" + name));
        }
    }

    @GetMapping("/suggest")
    @Timed(value = "destination.suggestDestinations", description = "Time taken to suggest destinations")
    public ResponseEntity<Map<String, Object>> suggestDestinations(@RequestParam("q") String query) {
        List<Map<String, String>> suggestions = new ArrayList<>();
        try {
            suggestions = destinationServiceImpl.suggestDestinations(query);
        } catch (Exception e) {
            logger.error("Error getting destination suggestions for query: {}", query, e);
            // Continue with empty suggestions rather than propagating error
        }

        Map<String, Object> response = new HashMap<>();
        response.put("suggestions", suggestions);
        response.put("count", suggestions.size());
        return ResponseEntity.ok(response);
    }
}
