package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Trip;
import com.travelmate.tripservice.model.TripLiteModel;
import com.travelmate.tripservice.model.TripModel;

import java.util.stream.Collectors;

public class TripMapper {
    public static TripModel toModel(Trip entity) {
        if (entity == null) return null;
        return new TripModel(entity.getId()
                , entity.getTitle()
                , entity.getDescription()
                , entity.getIsActive()
                , entity.getStartDate()
                , entity.getEndDate()
                , entity.getUpdatedAt()
                , entity.getPrice()
                , entity.getMainDestination() != null ? DestinationMapper.toModel(entity.getMainDestination()) : null
                , entity.getCreatedBy()
                , entity.getTripItineraryDetails().stream().map(TripItineraryDetailMapper::toModel).toList());
    }

    public static Trip toEntity(TripModel model) {
        if (model == null) return null;
        return Trip.builder()
                .id(model.id())
                .title(model.title())
                .description(model.description())
                .isActive(model.isActive())
                .startDate(model.startDate())
                .endDate(model.endDate())
                .updatedAt(model.updatedAt())
                .price(model.price())
                .mainDestination(DestinationMapper.toEntity(model.destination()))
                .createdBy(model.createdBy())
                .tripItineraryDetails(model.itineraryDetails().stream().map(TripItineraryDetailMapper::toEntity).collect(Collectors.toSet())).build();
    }

    public static TripLiteModel toLiteModel(Trip entity) {
        if (entity == null) return null;
        return TripLiteModel.builder().
                id(entity.getId())
                .isActive(entity.getIsActive())
                .title(entity.getTitle())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .updatedAt(entity.getUpdatedAt())
                .price(entity.getPrice())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
