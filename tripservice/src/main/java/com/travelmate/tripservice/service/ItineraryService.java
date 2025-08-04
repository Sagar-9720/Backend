package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.ItineraryModel;

import java.util.List;
import java.util.Optional;

public interface ItineraryService {
    ItineraryModel createItinerary(ItineraryModel itineraryModel);

    Optional<ItineraryModel> getItineraryById(Long id);

    List<ItineraryModel> getAllItineraries();

    ItineraryModel updateItinerary(Long id, ItineraryModel updatedItineraryModel);

    void deleteItinerary(Long id);

    List<ItineraryModel> getItinerariesByDestinationId(Long destinationId);


}
