package com.travelmate.tripservice.exceptions;

public class TripNotFoundException extends TripServiceException {
    public TripNotFoundException(Long id) {
        super("Trip with ID " + id + " not found.");

    }

    public TripNotFoundException(String name) {
        super("Trip with name '" + name + "' not found.");
    }
}

