package com.travelmate.tripservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Activity name cannot be empty.")
    private String activityName;

    private String description;

    @ManyToOne
    @JoinColumn(name = "trip_itinerary_detail_id", nullable = false)
    private TripItineraryDetail tripItineraryDetail;
}
