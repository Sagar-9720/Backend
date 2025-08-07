package com.travelmate.tripservice.dto;

import lombok.Data;

@Data
public class TripInteractionDTO {
    private String userId;
    private String tripId;
    private String type; // LIKE, COMMENT, SAVE
    private String content; // Used for comments
    private Long timestamp;
}
