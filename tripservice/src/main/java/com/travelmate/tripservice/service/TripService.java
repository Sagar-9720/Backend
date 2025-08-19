package com.travelmate.tripservice.service;

import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.model.TripLiteModel;
import com.travelmate.tripservice.model.TripModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TripService {

    //Admin operations
    TripLiteModel createTrip(String userName, String role, TripModel tripModel) throws TripExistsException;

    TripLiteModel updateTrip(String role, TripModel updatedTripModel) throws TripNotFoundException;

    TripLiteModel deleteTrip(String role, Long id) throws TripNotFoundException;

    void autoDeleteTripByDate(String role);

    List<TripRequest> getTripRequestByUserId(String authUserId, String role, String userId);

    TripLiteModel approveTripRequest(String role, String tripRequestId);

    List<TripRequest> getAllTripsRequested(String role);


    //User operations

    List<TripLiteModel> tripsBtwPriceRanges(String role, BigDecimal startPrice, BigDecimal endPrice);

    TripRequest addTripsRequestedByUser(TripRequest tripRequest);

    //Both Admin and User operations
    List<TripLiteModel> getTripsByDestinationName(String role, String destinationName) throws DestinationNotFoundException;

    List<TripLiteModel> getAllTrips(String role);

    TripModel getTripById(Long id) throws TripNotFoundException;

    List<String> suggestTrips(String query);

    void indexTrip(TripModel tripModel);

    List<Map<String, String>> getTripNamesById(List<String> tripIds);

}
