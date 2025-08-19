package com.travelmate.journalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.journalservice.dto.UserInfo;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;
import com.travelmate.journalservice.response.CustomResponseEntity;
import com.travelmate.journalservice.serviceimpl.TravelJournalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal/journals")
public class TravelJournalController {
    @Autowired
    private TravelJournalServiceImpl travelJournalService;

    public UserInfo extractHeader(String authHeader) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userInfo = objectMapper.readValue(authHeader, UserInfo.class);
        return userInfo;
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getAllJournals(@RequestHeader("X-UserInfo") String authHeader) throws Exception {
        UserInfo userInfo = extractHeader(authHeader);
        List<TravelJournalLiteModel> journals = travelJournalService.getAllJournals(userInfo.role());
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched", journals, "/journals"));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> createJournal(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TravelJournalModel journalModel) {
        try {
            TravelJournalModel created = travelJournalService.createJournal(journalModel);
            return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Journal created", created, "/journals"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to create journal: " + e.getMessage(), "/journals"));
        }
    }

    @PutMapping
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> updateJournal(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TravelJournalModel journalModel) {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            TravelJournalModel updated = travelJournalService.updateJournal(userInfo.userId(), journalModel);
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
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> deleteJournal(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String id) {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            TravelJournalModel deleted = travelJournalService.deleteJournal(userInfo.userId(), id);
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
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> getJournalById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String id) {
        TravelJournalModel journal = travelJournalService.getJournalById(id);
        if (journal != null) {
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal fetched", journal, "/journals/" + id));
        } else {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + id));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getJournalsByUserId(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String userId) throws Exception {
        UserInfo userInfo = extractHeader(authHeader);
        List<TravelJournalLiteModel> journals = travelJournalService.getJournalsByUserId(userInfo.role(), userInfo.userId(), userId);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for user", "/journals/user/" + userId));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by user", journals, "/journals/user/" + userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getJournalsByTripId(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String tripId) throws Exception {
        UserInfo userInfo = extractHeader(authHeader);
        List<TravelJournalLiteModel> journals = travelJournalService.getJournalsByTripId(userInfo.userId(), userInfo.role(), tripId);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for trip", "/journals/trip/" + tripId));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by trip", journals, "/journals/trip/" + tripId));
    }

    @GetMapping("/public")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getPublicJournals(@RequestHeader("X-UserInfo") String authHeader) {
        List<TravelJournalLiteModel> journals = travelJournalService.getPublicJournals();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Public journals fetched", journals, "/journals/public"));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> searchByTag(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String tag) throws Exception {
        UserInfo userInfo = extractHeader(authHeader);
        List<TravelJournalLiteModel> journals = travelJournalService.searchByTag(userInfo.userId(), tag);
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for tag", "/journals/tag/" + tag));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by tag", journals, "/journals/tag/" + tag));
    }
}
