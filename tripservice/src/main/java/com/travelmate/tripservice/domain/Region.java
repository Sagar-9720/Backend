package com.travelmate.tripservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Region name should not be blank")
    private String name;

    @ManyToOne(optional = false,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}

