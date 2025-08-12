package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.model.RegionModel;

public class RegionMapper {
    public static RegionModel toModel(Region entity) {
        if (entity == null) return null;
        return new RegionModel(
                entity.getId(),
                entity.getName(),
                CountryMapper.toModel(entity.getCountry())
        );
    }

    public static Region toEntity(RegionModel model) {
        if (model == null) return null;
        return Region.builder()
                .id(model.id())
                .name(model.name())
                .build();
    }
}
