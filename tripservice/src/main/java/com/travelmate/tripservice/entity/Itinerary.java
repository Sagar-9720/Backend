package com.travelmate.tripservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @NotBlank(message = "Itinerary Name cannot be empty.")
    @Column(nullable = false, name = "itinerary_name")
    private String itineraryName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @NotBlank(message = "Description cannot be empty.")
    @Column(columnDefinition = "TEXT")
    private String description;

}
