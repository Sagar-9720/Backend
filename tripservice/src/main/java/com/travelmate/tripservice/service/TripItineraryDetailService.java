package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.TripItineraryDetailModel;

import java.util.List;

public interface TripItineraryDetailService {
    TripItineraryDetailModel create(TripItineraryDetailModel model);

    TripItineraryDetailModel update(Long id, TripItineraryDetailModel model);

    TripItineraryDetailModel getById(Long id);

    List<TripItineraryDetailModel> getAll();
}
