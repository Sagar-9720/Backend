package com.travelmate.tripservice.exceptions;

public class ItineraryNotFoundException extends TripServiceException {
    public ItineraryNotFoundException(Long id) {
        super("Itinerary with ID " + id + " not found.");
    }
}

