package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.model.ItineraryModel;

import java.util.List;
import java.util.Optional;

public interface ItineraryService {

    //Admin Operations
    ItineraryModel createItinerary(String role, ItineraryModel itineraryModel);

    ItineraryModel updateItinerary(String role, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException;

    List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) throws DestinationNotFoundException;

    List<ItineraryModel> getAllItineraries();

    //Both Admin and User Operations
    List<ItineraryModel> suggestItineraries(String keyword, Long destinationId);

    ItineraryModel getItineraryById(Long id) throws ItineraryNotFoundException;
}
