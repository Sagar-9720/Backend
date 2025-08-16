package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.ItineraryActivity;
import com.travelmate.tripservice.model.ItineraryActivityModel;

public class ItineraryActivityMapper {
    public static ItineraryActivityModel toModel(ItineraryActivity entity) {
        if (entity == null) return null;
        return new ItineraryActivityModel(entity.getId(), entity.getActivityName(), entity.getDescription());
    }

    public static ItineraryActivity toEntity(ItineraryActivityModel model) {
        if (model == null) return null;
        ItineraryActivity entity = new ItineraryActivity();
        entity.setId(model.id());
        entity.setActivityName(model.activityName());
        entity.setDescription(model.description());
        return entity;
    }
}

