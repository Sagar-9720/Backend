package com.travelmate.tripservice.model;

public record RegionModel(
        Long id,
        String name,
        CountryModel country
) {
}
