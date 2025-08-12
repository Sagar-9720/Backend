package com.travelmate.tripservice.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "trip_requests")
public class TripRequest implements Serializable {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private Long mainDestinationId;
    private String requestedBy;
    @Builder.Default
    private Boolean approved = false;
    private List<RequestedItinerary> itineraries;
    private String createdBy;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedItinerary implements Serializable {
        private String id;
        private String itineraryName;
        private String itineraryDescription;
        private Destination mainDestination;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
    }
}
