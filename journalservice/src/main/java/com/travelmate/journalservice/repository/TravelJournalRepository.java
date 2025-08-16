package com.travelmate.journalservice.repository;

import com.travelmate.journalservice.entity.TravelJournal;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TravelJournalRepository extends MongoRepository<TravelJournal, String> {
    List<TravelJournal> findByUserId(String userId);
    List<TravelJournal> findByTripId(String tripId);
    List<TravelJournal> findByTagsContaining(String tag);
    List<TravelJournal> findByIsPublicTrue();
}

