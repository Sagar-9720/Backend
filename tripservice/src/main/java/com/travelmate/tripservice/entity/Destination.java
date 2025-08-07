package com.travelmate.tripservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @NotBlank(message = "Description can not be empty.")
    private String description;

    @Column(name = "image_url")
    @NotBlank(message = "Image URL must not be null")
    @Pattern(
            regexp = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$",
            message = "Invalid URL format"
    )
    private String imageUrl;

}


