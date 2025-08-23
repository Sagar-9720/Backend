package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.model.ItineraryModel;


public class ItineraryMapper {
    public static ItineraryModel toModel(Itinerary entity) {
        if (entity == null) return null;
        return new ItineraryModel(entity.getId(), entity.getItineraryName(), entity.getDestination() != null ? DestinationMapper.toModel(entity.getDestination()) : null, entity.getDescription());
    }

    public static Itinerary toEntity(ItineraryModel model) {
        if (model == null) return null;
        return Itinerary.builder().id(model.id()).itineraryName(model.itineraryName()).destination(DestinationMapper.toEntity(model.destination())).description(model.description()).build();
    }
}
