package com.travelmate.tripservice.controller;

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
    public ResponseEntity<CustomResponseEntity<DestinationModel>> create(@RequestHeader("Authorization") String token, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            DestinationModel saved = destinationServiceImpl.createDestination(destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination created", saved, "/api/destinations"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, e.getMessage(), "/api/destinations"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> update(@RequestHeader("Authorization") String token, @PathVariable Long id, @Valid @RequestBody DestinationModel destinationModel) {
        try {
            destinationModel.setId(id);
            DestinationModel updated = destinationServiceImpl.updateDestination(token, destinationModel);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination updated", updated, "/api/destinations/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(CustomResponseEntity.error(403, e.getMessage(), "/api/destinations/" + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            DestinationModel model = new DestinationModel();
            model.setId(id);
            destinationServiceImpl.deleteDestination(token, model);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination deleted", null, "/api/destinations/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(CustomResponseEntity.error(403, e.getMessage(), "/api/destinations/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> getById(@PathVariable Long id) {
        DestinationModel destination = destinationServiceImpl.getDestinationById(id).orElse(null);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination fetched", destination, "/api/destinations/" + id));
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getAll() {
        List<DestinationModel> destinations = destinationServiceImpl.getAllDestinations();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "All destinations fetched", destinations, "/api/destinations"));
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByRegion(@PathVariable Long regionId) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByRegionId(regionId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by region fetched", destinations, "/api/destinations/region/" + regionId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/region/" + regionId));
        }
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> getByCountry(@PathVariable Long countryId) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.getDestinationsByCountryId(countryId);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations by country fetched", destinations, "/api/destinations/country/" + countryId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/country/" + countryId));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> searchByName(@RequestParam String name) {
        try {
            List<DestinationModel> destinations = destinationServiceImpl.searchDestinationByName(name);
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations searched by name", destinations, "/api/destinations/search?name=" + name));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, e.getMessage(), "/api/destinations/search?name=" + name));
        }
    }
}
