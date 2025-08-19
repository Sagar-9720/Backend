package com.travelmate.journalservice.service;

import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;

import java.util.List;

public interface TravelJournalService {

    TravelJournalModel createJournal(TravelJournalModel journalModel);

    TravelJournalModel updateJournal(String userId, TravelJournalModel journalModel);

    TravelJournalModel deleteJournal(String userId, String id);

    TravelJournalModel getJournalById(String id);

    List<TravelJournalLiteModel> getJournalsByUserId(String role, String authUserId, String userId);

    List<TravelJournalLiteModel> getJournalsByTripId(String userId, String role, String tripId);

    List<TravelJournalLiteModel> getPublicJournals();

    List<TravelJournalLiteModel> searchByTag(String userId, String tag);

    List<TravelJournalLiteModel> getAllJournals(String role);

}
