package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.domain.Trip;
import com.travelmate.tripservice.model.TripModel;

public class TripMapper {
    public static TripModel toModel(Trip entity) {
        if (entity == null) return null;
        return TripModel.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .price(entity.getPrice())
                .mainDestinationId(entity.getMainDestination() != null ? entity.getMainDestination().getId() : null)
                .build();
    }

    public static Trip toEntity(TripModel model) {
        if (model == null) return null;
        return Trip.builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .price(model.getPrice())
                // mainDestination mapping should be handled in serviceImpl
                .build();
    }
}

