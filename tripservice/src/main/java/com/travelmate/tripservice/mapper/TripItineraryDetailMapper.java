package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.TripItineraryDetail;
import com.travelmate.tripservice.model.TripItineraryDetailModel;

public class TripItineraryDetailMapper {
    public static TripItineraryDetailModel toModel(TripItineraryDetail entity) {
        if (entity == null) return null;
        return new TripItineraryDetailModel(
                entity.getId(),
                ItineraryMapper.toModel(entity.getItinerary()),
                entity.getDayNumber(),
                entity.getArrivalTime(),
                entity.getDepartureTime(),
                entity.getActivities().stream().map(ItineraryActivityMapper::toModel).toList()
        );
    }

    public static TripItineraryDetail toEntity(TripItineraryDetailModel model) {
        if( model == null) return null;
        return new TripItineraryDetail().builder()
                .id(model.id())
                .itinerary(ItineraryMapper.toEntity(model.itinerary()))
                .dayNumber(model.dayNumber())
                .arrivalTime(model.arrivalTime())
                .departureTime(model.departureTime())
                .activities(model.activities().stream().map(ItineraryActivityMapper::toEntity).toList())
                .build();
    }
}

