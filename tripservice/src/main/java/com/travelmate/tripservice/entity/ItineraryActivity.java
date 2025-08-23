package com.travelmate.tripservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

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
    @Column(nullable = false)
    private String activityName;

    private String description;

    @ManyToMany(mappedBy = "activities")
    @JsonIgnore
    private Set<TripItineraryDetail> tripItineraryDetails;
}
