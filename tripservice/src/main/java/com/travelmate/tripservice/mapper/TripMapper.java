package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Trip;
import com.travelmate.tripservice.model.TripModel;

public class TripMapper {
    public static TripModel toModel(Trip entity) {
        if (entity == null) return null;
        return new TripModel(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPrice(),
                entity.getMainDestination() != null ? entity.getMainDestination().getId() : null,
                entity.getCreatedBy(),
                entity.getItineraries().stream().map(ItineraryMapper::toModel).toList()
        );
    }

    public static Trip toEntity(TripModel model) {
        if (model == null) return null;
        return Trip.builder()
                .id(model.id())
                .title(model.title())
                .description(model.description())
                .startDate(model.startDate())
                .endDate(model.endDate())
                .price(model.price())
                .createdBy(model.createdBy())
                .build();
    }
}
