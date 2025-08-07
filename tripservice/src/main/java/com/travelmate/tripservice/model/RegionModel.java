package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RegionModel {
    private Long id;
    private String name;
    private Long countryId;
}

