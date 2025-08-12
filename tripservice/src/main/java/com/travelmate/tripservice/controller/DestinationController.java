package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.serviceimpl.DestinationServiceImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/trip/destinations")
public class DestinationController {

    @Autowired
    private DestinationServiceImpl destinationServiceImpl;

    @PostMapping
    public ResponseEntity<CustomResponseEntity<DestinationModel>> create(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            DestinationModel saved = destinationServiceImpl.createDestination(token, destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination created", saved, "/api/destinations"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, e.getMessage(), "/api/destinations"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<DestinationModel>> update(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            DestinationModel updated = destinationServiceImpl.updateDestination(token, destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination updated", updated, "/api/destinations/" + updated.id()));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(CustomResponseEntity.error(403, e.getMessage(), "/api/destinations/"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            Destination destination = new Destination();
            destination.setId(id);
            destinationServiceImpl.deleteDestination(token, DestinationMapper.toModel(destination));
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination deleted", null, "/api/destinations/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(CustomResponseEntity.error(403, e.getMessage(), "/api/destinations/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> getById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        DestinationModel destination = destinationServiceImpl.getDestinationById(token, id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination fetched", destination, "/api/destinations/" + id));
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getAll(@RequestHeader("Authorization") String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<DestinationModel> destinations = destinationServiceImpl.getAllDestinations(token);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "All destinations fetched", destinations, "/api/destinations"));
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByRegion(@RequestHeader("Authorization") String authHeader, @PathVariable Long regionId) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByRegionId(token, regionId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by region fetched", destinations, "/api/destinations/region/" + regionId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/region/" + regionId));
        }
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByCountry(@RequestHeader("Authorization") String authHeader, @PathVariable Long countryId) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByCountryId(token, countryId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by country fetched", destinations, "/api/destinations/country/" + countryId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/country/" + countryId));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> searchByName(@RequestHeader("Authorization") String authHeader, @RequestParam String name) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            List<DestinationModel> destinations = destinationServiceImpl.searchDestinationByName(token, name);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations searched by name", destinations, "/api/destinations/search?name=" + name));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/search?name=" + name));
        }
    }
}
