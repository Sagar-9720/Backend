package com.travelmate.tripservice.exceptions;

public class DestinationExistException extends RuntimeException {
    public DestinationExistException(String name) {
        super("Destination with name '" + name + "' already exists.");
    }

    public DestinationExistException(Long id) {
        super("Destination with ID " + id + " already exists.");
    }
}
