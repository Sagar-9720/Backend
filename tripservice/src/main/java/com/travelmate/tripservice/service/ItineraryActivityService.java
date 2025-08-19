package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.ItineraryActivityModel;

import java.util.List;

public interface ItineraryActivityService {
    ItineraryActivityModel create(ItineraryActivityModel model);

    ItineraryActivityModel update(Long id, ItineraryActivityModel model);

    ItineraryActivityModel getById(Long id);

    List<ItineraryActivityModel> getAll();

    List<ItineraryActivityModel> suggest(String keyword);
}
