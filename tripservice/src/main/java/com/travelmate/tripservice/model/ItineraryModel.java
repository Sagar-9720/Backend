package com.travelmate.tripservice.model;


public record ItineraryModel(Long id, String itineraryName, DestinationModel destination, String description) {
}
