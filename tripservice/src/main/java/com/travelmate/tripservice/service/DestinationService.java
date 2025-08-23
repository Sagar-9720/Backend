package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.model.DestinationModel;

import java.util.List;

public interface DestinationService {

    //Admin Operations
    DestinationModel createDestination(String role, DestinationModel destinationModel) throws DestinationExistException;

    DestinationModel updateDestination(String role, DestinationModel destinationModel) throws DestinationNotFoundException;

    // Both Admin and User Operations
    DestinationModel getDestinationById(Long id) throws DestinationNotFoundException;

    List<DestinationModel> getAllDestinations();

    List<DestinationModel> getDestinationsByRegionId(Long regionId) throws RegionNotFoundException;

    List<DestinationModel> getDestinationsByCountryId(Long countryId) throws CountryNotFoundException;

    List<DestinationModel> searchDestinationByName(String name) throws DestinationNotFoundException;

    List<DestinationModel> suggestDestinations(String query);

    void indexDestination(DestinationModel destinationModel);
}
