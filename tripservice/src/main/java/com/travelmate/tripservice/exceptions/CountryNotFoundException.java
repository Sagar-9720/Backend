package com.travelmate.tripservice.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String name) {
        super("Country with name " + name + " not found");
    }

    public CountryNotFoundException(Long id) {
        super("Country with ID " + id + " not found");
    }
}
