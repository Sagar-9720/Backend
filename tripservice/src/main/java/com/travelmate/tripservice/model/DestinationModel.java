package com.travelmate.tripservice.model;

public record DestinationModel(
    Long id,
    String name,
    RegionModel region,
    String description,
    String imageUrl
) {}
