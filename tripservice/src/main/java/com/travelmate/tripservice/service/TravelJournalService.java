package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.TravelJournalModel;

import java.util.List;

public interface TravelJournalService {
    TravelJournalModel createJournal(TravelJournalModel journalModel);

    TravelJournalModel updateJournal(String id, TravelJournalModel journalModel);

    void deleteJournal(String id);

    TravelJournalModel getJournalById(String id);

    List<TravelJournalModel> getJournalsByUserId(String userId);

    List<TravelJournalModel> getJournalsByTripId(String tripId);

    List<TravelJournalModel> getPublicJournals();

    List<TravelJournalModel> searchByTag(String tag);
}
