package com.travelmate.journalservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;

import java.util.List;

public interface TravelJournalService {

    TravelJournalModel createJournal(String token, TravelJournalModel journalModel);

    TravelJournalModel updateJournal(String token, TravelJournalModel journalModel) throws JsonProcessingException;

    TravelJournalModel deleteJournal(String token, String id) throws JsonProcessingException;

    TravelJournalModel getJournalById(String token, String id);

    List<TravelJournalLiteModel> getJournalsByUserId(String token, String userId) throws JsonProcessingException;

    List<TravelJournalLiteModel> getJournalsByTripId(String token, String tripId) throws JsonProcessingException;

    List<TravelJournalLiteModel> getPublicJournals(String token);

    List<TravelJournalLiteModel> searchByTag(String tag, String token) throws JsonProcessingException;

    List<TravelJournalLiteModel> getAllJournals(String token) throws JsonProcessingException;


}
