package com.travelmate.tripservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "itinerary_name")
    @NotBlank(message = "Itinerary Name cannot be empty.")
    private String itineraryName;

    @JsonIgnore
    @ManyToMany(mappedBy = "itineraries", fetch = FetchType.LAZY)
    private List<Trip> trips;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Column(name="day_number")
    @NotBlank(message = "Day Number required.")
    private Integer dayNumber; // Day in itinerary (e.g., Day 1, Day 2)

    @NotBlank(message = "Description cannot be empty.")
    private String description; // Optional note for this destination/day

    @Column(name = "arrival_time")
    @NotNull
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time")
    @NotNull
    private LocalDateTime departureTime;

}

