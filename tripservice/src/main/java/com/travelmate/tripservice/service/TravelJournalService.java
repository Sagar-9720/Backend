package com.travelmate.tripservice.service;

import com.travelmate.tripservice.dto.TripInteractionDTO;
import com.travelmate.tripservice.model.TravelJournalModel;

import java.util.List;

public interface TravelJournalService {

    TravelJournalModel createJournal(String token, TravelJournalModel journalModel);

    TravelJournalModel updateJournal(String token, TravelJournalModel journalModel);

    TravelJournalModel deleteJournal(String token, String id);

    TravelJournalModel getJournalById(String token, String id);

    List<TravelJournalModel> getJournalsByUserId(String token, String userId);

    List<TravelJournalModel> getJournalsByTripId(String token, String tripId);

    List<TravelJournalModel> getPublicJournals(String token);

    List<TravelJournalModel> searchByTag(String tag,String token);

    List<TravelJournalModel> getAllJournals(String token);
}
