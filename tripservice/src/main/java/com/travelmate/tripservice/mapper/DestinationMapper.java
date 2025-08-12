package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.model.DestinationModel;

public class DestinationMapper {
    public static DestinationModel toModel(Destination entity) {
        if (entity == null) return null;
        return new DestinationModel(entity.getId(), entity.getName(), RegionMapper.toModel(entity.getRegion()), entity.getDescription(), entity.getImageUrl());
    }

    public static Destination toEntity(DestinationModel model) {
        if (model == null) return null;
        return Destination.builder().id(model.id()).name(model.name()).description(model.description()).imageUrl(model.imageUrl()).build();
    }
}
