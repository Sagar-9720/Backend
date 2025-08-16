package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryActivityModel;

import java.util.List;

public interface ItineraryActivityService {
    ItineraryActivityModel create(String token, ItineraryActivityModel model) throws UnauthorizedAccessException;

    ItineraryActivityModel update(String token, Long id, ItineraryActivityModel model) throws UnauthorizedAccessException;

    void delete(String token, Long id) throws UnauthorizedAccessException;

    ItineraryActivityModel getById(String token, Long id) throws UnauthorizedAccessException;

    List<ItineraryActivityModel> getAll(String token) throws UnauthorizedAccessException;

    List<ItineraryActivityModel> suggest(String token, String keyword) throws UnauthorizedAccessException;
}
