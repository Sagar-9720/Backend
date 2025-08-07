package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.model.DestinationModel;

public class DestinationMapper {
    public static DestinationModel toModel(Destination entity) {
        if (entity == null) return null;
        return DestinationModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .regionId(entity.getRegion() != null ? entity.getRegion().getId() : null)
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public static Destination toEntity(DestinationModel model) {
        if (model == null) return null;
        return Destination.builder()
                .id(model.getId())
                .name(model.getName())
                // region mapping should be handled in serviceImpl
                .description(model.getDescription())
                .imageUrl(model.getImageUrl())
                .build();
    }
}

