package com.travelmate.tripservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripItineraryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private Itinerary itinerary;

    @Column(name = "day_number")
    @NotNull(message = "Day Number is required.")
    private Integer dayNumber;

    @Column(name = "arrival_time")
    @NotNull
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time")
    @NotNull
    private LocalDateTime departureTime;

    @OneToMany(mappedBy = "tripItineraryDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryActivity> activities;
}
