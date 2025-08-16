package com.travelmate.tripservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    @Column(nullable = false, name = "itinerary_name")
    @NotBlank(message = "Itinerary Name cannot be empty.")
    private String itineraryName;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @NotBlank(message = "Description cannot be empty.")
    private String description; // Generic description of this itinerary

    @JsonIgnore
    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL)
    private List<TripItineraryDetail> tripDetails; // All trip-specific schedules linked to this itinerary
}
