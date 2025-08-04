package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationModel {
    private Long id;
    private String name;
    private Long regionId;
    private String description;
    private String imageUrl;
}

