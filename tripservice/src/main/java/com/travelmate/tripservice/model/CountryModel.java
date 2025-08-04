package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryModel {
    private Long id;
    private String name;
}

