package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.ItineraryActivityModel;

import java.util.List;
import java.util.Map;

public interface ItineraryActivityService {
    ItineraryActivityModel create(ItineraryActivityModel model);

    ItineraryActivityModel update(Long id, ItineraryActivityModel model);

    ItineraryActivityModel getById(Long id);

    List<ItineraryActivityModel> getAll();

    List<Map<String, String>> suggest(String keyword);
}
