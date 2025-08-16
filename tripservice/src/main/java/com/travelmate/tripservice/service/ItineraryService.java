package com.travelmate.tripservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryModel;

import java.util.List;
import java.util.Optional;

public interface ItineraryService {

    //Admin Operations
    ItineraryModel createItinerary(String token, ItineraryModel itineraryModel) throws UnauthorizedAccessException, JsonProcessingException;

    ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    ItineraryModel deleteItinerary(String token, Long id) throws ItineraryNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    List<ItineraryModel> getItinerariesByDestinationId(String token, Long destinationId) throws DestinationNotFoundException, JsonProcessingException;

    List<ItineraryModel> getAllItineraries(String token) throws UnauthorizedAccessException, JsonProcessingException;


    //User Operations


    //Both Admin and User Operations
    List<ItineraryModel> suggestItineraries(String token, String keyword, Long destinationId);

    Optional<ItineraryModel> getItineraryById(String token, Long id) throws ItineraryNotFoundException;


}
