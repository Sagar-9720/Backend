package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.DestinationModel;

import java.util.List;
import java.util.Optional;

public interface DestinationService {

    DestinationModel createDestination(DestinationModel destinationModel);
    Optional<DestinationModel> getDestinationById(Long id);
    List<DestinationModel> getAllDestinations();
    DestinationModel deleteDestination(String token, DestinationModel destinationModel);

    DestinationModel updateDestination(String token, DestinationModel destinationModel);

    List<DestinationModel> getDestinationsByRegionId(Long regionId);

    List<DestinationModel> getDestinationsByCountryId(Long countryId);

    List<DestinationModel> searchDestinationByName(String name);

}
