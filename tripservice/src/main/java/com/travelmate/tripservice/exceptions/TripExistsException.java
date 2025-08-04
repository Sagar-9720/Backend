package com.travelmate.tripservice.exceptions;

public class TripExistsException extends RuntimeException {
    public TripExistsException(String name) {
        super("Trip with name " + name + " already exists");
    }
}
