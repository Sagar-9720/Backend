package com.travelmate.tripservice.exceptions;

public class TravelJournalNotFoundException extends TripServiceException {
    public TravelJournalNotFoundException(String id) {
        super("Travel journal with ID " + id + " not found.");
    }
}

