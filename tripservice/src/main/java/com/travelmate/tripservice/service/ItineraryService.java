package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.ItineraryModel;
import java.util.List;
import java.util.Optional;

public interface ItineraryService {
    ItineraryModel createItinerary(String token, ItineraryModel itineraryModel);
    Optional<ItineraryModel> getItineraryById(Long id);
    List<ItineraryModel> getAllItineraries();
    ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel);
    ItineraryModel deleteItinerary(String token, Long id);
    List<ItineraryModel> getItinerariesByDestinationId(Long destinationId);

}
