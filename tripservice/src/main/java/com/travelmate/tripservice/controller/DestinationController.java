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
@RequestMapping("/api/destinations")
public class DestinationController {

    @Autowired
    private DestinationServiceImpl destinationServiceImpl;

    @PostMapping
    public ResponseEntity<CustomResponseEntity<DestinationModel>> create(@Valid @RequestBody DestinationModel destinationModel) {
        DestinationModel saved = destinationServiceImpl.createDestination(destinationModel);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination created", saved, "/api/destinations"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<DestinationModel>> update(@PathVariable Long id, @Valid @RequestBody DestinationModel destinationModel) {
        DestinationModel updated = destinationServiceImpl.updateDestination(id, destinationModel);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination updated", updated, "/api/destinations/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> delete(@PathVariable Long id) {
        destinationServiceImpl.deleteDestination(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destination deleted", null, "/api/destinations/" + id));
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

    @GetMapping("/search")
    public ResponseEntity<CustomResponseEntity<List<DestinationModel>>> searchByName(@RequestParam String name) {
        List<DestinationModel> destinations = destinationServiceImpl.searchDestinationsByName(name);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Destinations searched by name", destinations, "/api/destinations/search?name=" + name));
    }
}
