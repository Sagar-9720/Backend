package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.domain.Itinerary;
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
@RequestMapping("/itineraries")
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
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary fetched", itinerary.orElse(null), "/itineraries/" + id));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> createItinerary(@RequestBody ItineraryModel itineraryModel) {
        ItineraryModel created = itineraryService.createItinerary(itineraryModel);
        return ResponseEntity.ok(CustomResponseEntity.success(201, "Itinerary created", created, "/itineraries"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<ItineraryModel>> updateItinerary(@PathVariable Long id, @RequestBody ItineraryModel itineraryModel) {
        ItineraryModel updated = itineraryService.updateItinerary(id, itineraryModel);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary updated", updated, "/itineraries/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteItinerary(@PathVariable Long id) {
        itineraryService.deleteItinerary(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Itinerary deleted", null, "/itineraries/" + id));
    }
}
