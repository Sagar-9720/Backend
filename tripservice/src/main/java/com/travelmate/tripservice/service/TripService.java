package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.TripModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripService {
    TripModel createTrip(TripModel tripModel) throws Exception;
    Optional<TripModel> getTripById(Long id) throws Exception;
    List<TripModel> getAllTrips();
    TripModel updateTrip(Long id, TripModel updatedTripModel) throws Exception;
    TripModel deleteTrip(Long id) throws Exception;
    List<TripModel> getTripsByDestinationName(String destinationName);
    List<TripModel> tripsBtwPriceRanges(BigDecimal startPrice, BigDecimal endPrice);
    void autoDeleteTripByDate(Long tripId);

    List<TripModel> getTripRequestByUserId(String userId) throws Exception;

    List<TripModel> addTripsRequestedByUser(TripModel tripModel) throws Exception;
}
