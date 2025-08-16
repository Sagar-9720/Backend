package com.travelmate.tripservice.model;

import com.travelmate.tripservice.entity.Destination;

public record ItineraryModel(Long id, String itineraryName, Destination destination, String description) {
}
