package com.travelmate.tripservice.exceptions;

public class RegionNotFoundException extends RuntimeException {
    public RegionNotFoundException(String name) {
        super("Region with name " + name + " not found");
    }

    public RegionNotFoundException(Long id) {
        super("Region with ID " + id + " not found");
    }
}
