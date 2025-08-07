package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ItineraryModel {
    private Long id;
    private String itineraryName;
    private Long destinationId;
    private Integer dayNumber;
    private String description;
    private String arrivalTime;
    private String departureTime;
}

