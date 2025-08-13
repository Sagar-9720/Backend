package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.TravelJournalModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.TravelJournalService;
import com.travelmate.tripservice.serviceimpl.TravelJournalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip/journals")
public class TravelJournalController {
    @Autowired
    private TravelJournalServiceImpl travelJournalService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getAllJournals(@RequestHeader("Authorization") String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<TravelJournalModel> journals = travelJournalService.getAllJournals(token);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched", journals, "/journals"));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> createJournal(@RequestHeader("Authorization") String authHeader, @RequestBody TravelJournalModel journalModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            TravelJournalModel created = travelJournalService.createJournal(token, journalModel);
            return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Journal created", created, "/journals"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to create journal: " + e.getMessage(), "/journals"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> updateJournal(@RequestHeader("Authorization") String authHeader, @RequestBody TravelJournalModel journalModel) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            TravelJournalModel updated = travelJournalService.updateJournal(token, journalModel);
            if (updated != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal updated", updated, "/journals/" + journalModel.id()));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + journalModel.id()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to update journal: " + e.getMessage(), "/journals/" + journalModel.id()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> deleteJournal(@RequestHeader("Authorization") String authHeader, @PathVariable String id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            TravelJournalModel deleted = travelJournalService.deleteJournal(token, id);
            if (deleted != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal deleted", deleted, "/journals/" + id));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to delete journal: " + e.getMessage(), "/journals/" + id));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> getJournalById(@RequestHeader("Authorization") String authHeader, @PathVariable String id) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        TravelJournalModel journal = travelJournalService.getJournalById(token, id);
        if (journal != null) {
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal fetched", journal, "/journals/" + id));
        } else {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + id));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getJournalsByUserId(@RequestHeader("Authorization") String authHeader, @PathVariable String userId) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<TravelJournalModel> journals = travelJournalService.getJournalsByUserId(token, userId);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for user", "/journals/user/" + userId));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by user", journals, "/journals/user/" + userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getJournalsByTripId(@RequestHeader("Authorization") String authHeader, @PathVariable String tripId) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<TravelJournalModel> journals = travelJournalService.getJournalsByTripId(token, tripId);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for trip", "/journals/trip/" + tripId));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by trip", journals, "/journals/trip/" + tripId));
    }

    @GetMapping("/public")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getPublicJournals(@RequestHeader("Authorization") String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        // If token is provided, fetch public journals for the
        List<TravelJournalModel> journals = travelJournalService.getPublicJournals(token);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Public journals fetched", journals, "/journals/public"));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> searchByTag(@RequestHeader("Authorization") String authHeader, @PathVariable String tag) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<TravelJournalModel> journals = travelJournalService.searchByTag(token, tag);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for tag", "/journals/tag/" + tag));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by tag", journals, "/journals/tag/" + tag));
    }
}
