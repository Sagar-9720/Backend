package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.dto.TripDTO;
import com.travelmate.tripservice.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    
    @Autowired
    private TripService tripService;

    @GetMapping
    public ResponseEntity<Page<TripDTO>> getAllTrips(
            @RequestHeader("Authorization") String token,
            @RequestParam String userId,
            Pageable pageable) {
        try {
            Page<TripDTO> trips = tripService.getAllTripsForUser(userId, token, pageable);
            return ResponseEntity.ok(trips);
        } catch (IllegalStateException e) {
            return ResponseEntity.unauthorized().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
