package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.TripModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripService {
    TripModel createTrip(TripModel tripModel);

    Optional<TripModel> getTripById(Long id);

    List<TripModel> getAllTrips();

    TripModel updateTrip(Long id, TripModel updatedTripModel);

    void deleteTrip(Long id);

    List<TripModel> getTripsByDestinationName(String destinationName);

    List<TripModel> tripsBtwPriceRanges(BigDecimal startPrice, BigDecimal endPrice);

    void autoDeleteTripByDate(Long tripId);
}
