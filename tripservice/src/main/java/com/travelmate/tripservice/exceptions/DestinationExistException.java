package com.travelmate.tripservice.exceptions;

public class DestinationExistException extends RuntimeException {
    public DestinationExistException(String name) {
        super("Destination with name '" + name + "' already exists.");
    }
}
