package com.travelmate.tripservice.exceptions;

public class DestinationNotFoundException extends TripServiceException {
    public DestinationNotFoundException(Long id) {
        super("Destination with ID " + id + " not found.");
    }
    public DestinationNotFoundException(String name) {
        super("Destination with name '" + name + "' not found.");
    }

}

