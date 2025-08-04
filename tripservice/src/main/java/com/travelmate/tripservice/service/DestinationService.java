package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.DestinationModel;

import java.util.List;
import java.util.Optional;

public interface DestinationService {

    DestinationModel createDestination(DestinationModel destinationModel);

    Optional<DestinationModel> getDestinationById(Long id);

    List<DestinationModel> getAllDestinations();

    DestinationModel updateDestination(Long id, DestinationModel updatedDestinationModel);

    void deleteDestination(Long id);

    List<DestinationModel> searchDestinationsByName(String name);
}
