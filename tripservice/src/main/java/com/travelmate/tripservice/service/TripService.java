package com.travelmate.tripservice.service;

import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.model.TripModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripService {
    TripModel createTrip(String token, TripModel tripModel) throws ItineraryNotFoundException;
    Optional<TripModel> getTripById(Long id) throws Exception;
    List<TripModel> getAllTrips();
    TripModel updateTrip(String token, TripModel updatedTripModel) throws Exception;
    TripModel deleteTrip(String token, Long id) throws Exception;
    List<TripModel> getTripsByDestinationName(String destinationName);
    List<TripModel> tripsBtwPriceRanges(BigDecimal startPrice, BigDecimal endPrice);
    void autoDeleteTripByDate();

    List<TripModel> getTripRequestByUserId(String userId) throws Exception;

    List<TripModel> addTripsRequestedByUser(String token, TripModel tripModel) throws Exception;

    TripModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws Exception;
}
