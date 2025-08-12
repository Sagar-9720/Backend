package com.travelmate.tripservice.model;

public record ItineraryModel(Long id, String itineraryName, Long destinationId, Integer dayNumber, String description,
                             String arrivalTime, String departureTime) {
}
