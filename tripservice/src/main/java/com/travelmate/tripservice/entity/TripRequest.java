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
    private String requestedBy;     // User who requested this trip
    @Builder.Default
    private Boolean approved = false;

    private List<TripItineraryDetailRequest> itineraries;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripItineraryDetailRequest implements Serializable {
        private RequestItinerary requestItinerary;
        private Destination destination;
        private Integer dayNumber;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private List<RequestedActivity> activities;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestItinerary implements Serializable {
        private String id;
        private String name; // Name of the itinerary
        private String description;
        private RequestedDestination destination;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedDestination implements Serializable {
        private String name;
        private String description;
        private String region;
        private String country;
        private String imageUrl;
    }

    /**
     * User-requested activity details for a day
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedActivity implements Serializable {
        private String activityName;
        private String activityDescription;
    }
}
