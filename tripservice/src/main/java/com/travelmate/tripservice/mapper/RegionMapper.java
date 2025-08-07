package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.model.RegionModel;

public class RegionMapper {
    public static RegionModel toModel(Region entity) {
        if (entity == null) return null;
        return RegionModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .countryId(entity.getCountry() != null ? entity.getCountry().getId() : null)
                .build();
    }

    public static Region toEntity(RegionModel model) {
        if (model == null) return null;
        return Region.builder()
                .id(model.getId())
                .name(model.getName())
                // country mapping should be handled in serviceImpl
                .build();
    }
}

