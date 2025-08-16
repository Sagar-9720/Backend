package com.travelmate.journalservice.exceptions;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Long id) {
        super("Tag with ID " + id + " not found.");
    }
}

