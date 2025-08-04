package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.TravelJournalModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.TravelJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/journals")
public class TravelJournalController {
    @Autowired
    private TravelJournalService travelJournalService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getAllJournals() {
        List<TravelJournalModel> journals = travelJournalService.getPublicJournals();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched", journals, "/journals"));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> createJournal(@RequestBody TravelJournalModel journalModel) {
        TravelJournalModel created = travelJournalService.createJournal(journalModel);
        return ResponseEntity.ok(CustomResponseEntity.success(201, "Journal created", created, "/journals"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> updateJournal(@PathVariable String id, @RequestBody TravelJournalModel journalModel) {
        TravelJournalModel updated = travelJournalService.updateJournal(id, journalModel);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal updated", updated, "/journals/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteJournal(@PathVariable String id) {
        travelJournalService.deleteJournal(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal deleted", null, "/journals/" + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TravelJournalModel>> getJournalById(@PathVariable String id) {
        TravelJournalModel journal = travelJournalService.getJournalById(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journal fetched", journal, "/journals/" + id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getJournalsByUserId(@PathVariable String userId) {
        List<TravelJournalModel> journals = travelJournalService.getJournalsByUserId(userId);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by user", journals, "/journals/user/" + userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getJournalsByTripId(@PathVariable String tripId) {
        List<TravelJournalModel> journals = travelJournalService.getJournalsByTripId(tripId);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by trip", journals, "/journals/trip/" + tripId));
    }

    @GetMapping("/public")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> getPublicJournals() {
        List<TravelJournalModel> journals = travelJournalService.getPublicJournals();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Public journals fetched", journals, "/journals/public"));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<CustomResponseEntity<List<TravelJournalModel>>> searchByTag(@PathVariable String tag) {
        List<TravelJournalModel> journals = travelJournalService.searchByTag(tag);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Journals fetched by tag", journals, "/journals/tag/" + tag));
    }
}
