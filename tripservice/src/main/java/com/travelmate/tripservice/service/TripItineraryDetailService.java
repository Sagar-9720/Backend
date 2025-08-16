package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.TripItineraryDetailModel;

import java.util.List;

public interface TripItineraryDetailService {
    TripItineraryDetailModel create(String token, TripItineraryDetailModel model) throws UnauthorizedAccessException;

    TripItineraryDetailModel update(String token, Long id, TripItineraryDetailModel model) throws UnauthorizedAccessException;

    void delete(String token, Long id) throws UnauthorizedAccessException;

    TripItineraryDetailModel getById(String token, Long id) throws UnauthorizedAccessException;

    List<TripItineraryDetailModel> getAll(String token) throws UnauthorizedAccessException;
}

