package com.travelmate.tripservice.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String name) {
        super("Country with name " + name + " not found");
    }
}
