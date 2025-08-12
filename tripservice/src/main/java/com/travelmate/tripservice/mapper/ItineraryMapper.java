package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.model.ItineraryModel;

import java.time.LocalDateTime;

public class ItineraryMapper {
    public static ItineraryModel toModel(Itinerary entity) {
        if (entity == null) return null;
        return new ItineraryModel(
                entity.getId(),
                entity.getItineraryName(),
                entity.getDestination() != null ? entity.getDestination().getId() : null,
                entity.getDayNumber(),
                entity.getDescription(),
                entity.getArrivalTime() != null ? entity.getArrivalTime().toString() : null,
                entity.getDepartureTime() != null ? entity.getDepartureTime().toString() : null
        );
    }

    public static Itinerary toEntity(ItineraryModel model) {
        if (model == null) return null;
        return Itinerary.builder()
                .id(model.id())
                .itineraryName(model.itineraryName())
                .dayNumber(model.dayNumber())
                .description(model.description())
                .arrivalTime(model.arrivalTime() != null ? LocalDateTime.parse(model.arrivalTime()) : null)
                .departureTime(model.departureTime() != null ? LocalDateTime.parse(model.departureTime()) : null)
                .build();
    }
}
