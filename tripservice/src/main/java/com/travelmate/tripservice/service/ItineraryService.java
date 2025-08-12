package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryModel;

import java.util.List;
import java.util.Optional;

public interface ItineraryService {
    ItineraryModel createItinerary(String token, ItineraryModel itineraryModel) throws UnauthorizedAccessException;

    Optional<ItineraryModel> getItineraryById(Long id) throws ItineraryNotFoundException;

    List<ItineraryModel> getAllItineraries();

    ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException;

    ItineraryModel deleteItinerary(String token, Long id) throws ItineraryNotFoundException, UnauthorizedAccessException;

    List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) throws DestinationNotFoundException;

}
