package com.travelmate.tripservice.entity;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(optional = false)
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

    @OneToMany(mappedBy = "tripItineraryDetail", cascade = CascadeType.ALL)
    private List<ItineraryActivity> activities; // Separate table for activities
}
