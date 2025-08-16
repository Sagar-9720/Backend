package com.travelmate.tripservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.model.DestinationModel;

import java.util.List;
import java.util.Optional;

public interface DestinationService {

    //Admin Operations
    DestinationModel createDestination(String token, DestinationModel destinationModel) throws DestinationExistException, UnauthorizedAccessException, JsonProcessingException;

    DestinationModel deleteDestination(String token, DestinationModel destinationModel) throws DestinationNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    DestinationModel updateDestination(String token, DestinationModel destinationModel) throws DestinationNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    // Both Admin and User Operations
    DestinationModel getDestinationById(String token, Long id) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> getAllDestinations(String token) throws UnauthorizedAccessException;

    List<DestinationModel> getDestinationsByRegionId(String token, Long regionId) throws RegionNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> getDestinationsByCountryId(String token, Long countryId) throws CountryNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> searchDestinationByName(String token, String name) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<String> suggestDestinations(String query);

    void indexDestination(DestinationModel destinationModel);
}
