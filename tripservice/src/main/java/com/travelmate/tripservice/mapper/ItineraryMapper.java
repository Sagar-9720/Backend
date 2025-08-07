package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.model.ItineraryModel;

public class ItineraryMapper {
    public static ItineraryModel toModel(Itinerary entity) {
        if (entity == null) return null;
        return ItineraryModel.builder()
                .id(entity.getId())
                .itineraryName(entity.getItineraryName())
                .destinationId(entity.getDestination() != null ? entity.getDestination().getId() : null)
                .dayNumber(entity.getDayNumber())
                .description(entity.getDescription())
                .build();
    }

    public static Itinerary toEntity(ItineraryModel model) {
        if (model == null) return null;
        return Itinerary.builder()
                .id(model.getId())
                .itineraryName(model.getItineraryName())
                // destination mapping should be handled in serviceImpl
                .dayNumber(model.getDayNumber())
                .description(model.getDescription())
                .build();
    }
}

