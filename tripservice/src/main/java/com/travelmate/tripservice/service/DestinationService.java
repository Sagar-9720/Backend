package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.model.DestinationModel;

import java.util.List;
import java.util.Optional;

public interface DestinationService {

    DestinationModel createDestination(String token, DestinationModel destinationModel) throws DestinationExistException, UnauthorizedAccessException;

    DestinationModel getDestinationById(String token, Long id) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> getAllDestinations(String token) throws UnauthorizedAccessException;

    DestinationModel deleteDestination(String token, DestinationModel destinationModel) throws DestinationNotFoundException, UnauthorizedAccessException;

    DestinationModel updateDestination(String token, DestinationModel destinationModel) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> getDestinationsByRegionId(String token, Long regionId) throws RegionNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> getDestinationsByCountryId(String token, Long countryId) throws CountryNotFoundException, UnauthorizedAccessException;

    List<DestinationModel> searchDestinationByName(String token, String name) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<String> suggestDestinations(String query);
    void indexDestination(DestinationModel destinationModel);
}
