package com.travelmate.journalservice.exceptions;

public class TravelJournalNotFoundException extends RuntimeException {
    public TravelJournalNotFoundException(String id) {
        super("Travel journal with ID " + id + " not found.");
    }
}

