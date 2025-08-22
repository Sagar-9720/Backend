package com.travelmate.journalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.journalservice.dto.UserInfo;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;
import com.travelmate.journalservice.response.CustomResponseEntity;
import com.travelmate.journalservice.serviceimpl.TravelJournalServiceImpl;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal/journals")
@Timed(value = "journal.controller", description = "Travel journal controller timing metrics")
public class TravelJournalController {
    private static final Logger logger = LoggerFactory.getLogger(TravelJournalController.class);

    @Autowired
    private TravelJournalServiceImpl travelJournalService;

    public UserInfo extractHeader(String authHeader) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userInfo = objectMapper.readValue(authHeader, UserInfo.class);
        return userInfo;
    }

    @GetMapping
    @Timed(value = "journal.getAllJournals", description = "Time taken to fetch all journals")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getAllJournals(@RequestHeader("X-UserInfo") String authHeader) throws Exception {
        UserInfo userInfo = extractHeader(authHeader);
        logger.info("Fetching all journals for user role: {}", userInfo.role());
        List<TravelJournalLiteModel> journals = travelJournalService.getAllJournals(userInfo.role());
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched", journals, "/journals"));
    }

    @PostMapping
    @Timed(value = "journal.createJournal", description = "Time taken to create a journal")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> createJournal(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TravelJournalModel journalModel) {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Creating journal for user: {}", userInfo.userId());
            TravelJournalModel created = travelJournalService.createJournal(journalModel);
            return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Journal created", created, "/journals"));
        } catch (Exception e) {
            logger.error("Failed to create journal: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to create journal: " + e.getMessage(), "/journals"));
        }
    }

    @PutMapping
    @Timed(value = "journal.updateJournal", description = "Time taken to update a journal")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> updateJournal(@RequestHeader("X-UserInfo") String authHeader, @RequestBody TravelJournalModel journalModel) {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Updating journal ID: {} for user: {}", journalModel.id(), userInfo.userId());
            TravelJournalModel updated = travelJournalService.updateJournal(userInfo.userId(), journalModel);
            if (updated != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal updated", updated, "/journals/" + journalModel.id()));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found or access denied", "/journals/" + journalModel.id()));
            }
        } catch (Exception e) {
            logger.error("Failed to update journal: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to update journal: " + e.getMessage(), "/journals"));
        }
    }

    @DeleteMapping("/{id}")
    @Timed(value = "journal.deleteJournal", description = "Time taken to delete a journal")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> deleteJournal(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String id) {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Deleting journal ID: {} for user: {}", id, userInfo.userId());
            TravelJournalModel deleted = travelJournalService.deleteJournal(userInfo.userId(), id);
            if (deleted != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal deleted", deleted, "/journals/" + id));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + id));
            }
        } catch (Exception e) {
            logger.error("Failed to delete journal: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(CustomResponseEntity.error(400, "Failed to delete journal: " + e.getMessage(), "/journals/" + id));
        }
    }

    @GetMapping("/{id}")
    @Timed(value = "journal.getJournalById", description = "Time taken to fetch a journal by ID")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> getJournalById(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String id) {
        try {
            logger.info("Fetching journal with ID: {}", id);
            TravelJournalModel journal = travelJournalService.getJournalById(id);
            if (journal != null) {
                return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal fetched", journal, "/journals/" + id));
            } else {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Journal not found", "/journals/" + id));
            }
        } catch (Exception e) {
            logger.error("Error fetching journal: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(CustomResponseEntity.error(500, "Error fetching journal: " + e.getMessage(), "/journals/" + id));
        }
    }

    @GetMapping("/user/{userId}")
    @Timed(value = "journal.getJournalsByUserId", description = "Time taken to fetch journals by user ID")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getJournalsByUserId(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String userId) throws Exception {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Fetching journals for user: {}", userId);
            List<TravelJournalLiteModel> journals = travelJournalService.getJournalsByUserId(userInfo.role(), userInfo.userId(), userId);
            if (journals == null || journals.isEmpty()) {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for user", "/journals/user/" + userId));
            }
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by user", journals, "/journals/user/" + userId));
        } catch (Exception e) {
            logger.error("Error fetching journals by user ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(CustomResponseEntity.error(500, "Error fetching journals: " + e.getMessage(), "/journals/user/" + userId));
        }
    }

    @GetMapping("/trip/{tripId}")
    @Timed(value = "journal.getJournalsByTripId", description = "Time taken to fetch journals by trip ID")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getJournalsByTripId(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String tripId) throws Exception {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Fetching journals for trip: {}", tripId);
            List<TravelJournalLiteModel> journals = travelJournalService.getJournalsByTripId(userInfo.userId(), userInfo.role(), tripId);
            if (journals == null || journals.isEmpty()) {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for trip", "/journals/trip/" + tripId));
            }
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by trip", journals, "/journals/trip/" + tripId));
        } catch (Exception e) {
            logger.error("Error fetching journals by trip ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(CustomResponseEntity.error(500, "Error fetching journals: " + e.getMessage(), "/journals/trip/" + tripId));
        }
    }

    @GetMapping("/public")
    @Timed(value = "journal.getPublicJournals", description = "Time taken to fetch public journals")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> getPublicJournals(@RequestHeader("X-UserInfo") String authHeader) {
        try {
            logger.info("Fetching public journals");
            List<TravelJournalLiteModel> journals = travelJournalService.getPublicJournals();
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Public journals fetched", journals, "/journals/public"));
        } catch (Exception e) {
            logger.error("Error fetching public journals: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(CustomResponseEntity.error(500, "Error fetching public journals: " + e.getMessage(), "/journals/public"));
        }
    }

    @GetMapping("/tag/{tag}")
    @Timed(value = "journal.searchByTag", description = "Time taken to search journals by tag")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalLiteModel>>> searchByTag(@RequestHeader("X-UserInfo") String authHeader, @PathVariable String tag) throws Exception {
        try {
            UserInfo userInfo = extractHeader(authHeader);
            logger.info("Searching journals with tag: {}", tag);
            List<TravelJournalLiteModel> journals = travelJournalService.searchByTag(userInfo.userId(), tag);
            if (journals == null || journals.isEmpty()) {
                return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "No journals found for tag", "/journals/tag/" + tag));
            }
            return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by tag", journals, "/journals/tag/" + tag));
        } catch (Exception e) {
            logger.error("Error searching journals by tag: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(CustomResponseEntity.error(500, "Error searching journals by tag: " + e.getMessage(), "/journals/tag/" + tag));
        }
    }
}
