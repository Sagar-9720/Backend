package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryModel {
    private Long id;
    private String itineraryName;
    private Long destinationId;
    private Integer dayNumber;
    private String description;
}

