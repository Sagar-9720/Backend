package com.travelmate.tripservice.exceptions;

public class TagNotFoundException extends TripServiceException {
    public TagNotFoundException(Long id) {
        super("Tag with ID " + id + " not found.");
    }
}

