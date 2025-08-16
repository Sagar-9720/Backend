package com.travelmate.tripservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    TripLiteModel createTrip(String token, TripModel tripModel) throws TripExistsException, UnauthorizedAccessException, JsonProcessingException;

    TripLiteModel updateTrip(String token, TripModel updatedTripModel) throws TripNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    TripLiteModel deleteTrip(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException, JsonProcessingException;

    void autoDeleteTripByDate(String token) throws UnauthorizedAccessException, JsonProcessingException;

    List<TripRequest> getTripRequestByUserId(String token, String userId) throws UnauthorizedAccessException, JsonProcessingException;

    TripLiteModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws UnauthorizedAccessException, JsonProcessingException;

    List<TripRequest> getAllTripsRequested(String token) throws UnauthorizedAccessException, JsonProcessingException;


    //User operations

    List<TripLiteModel> tripsBtwPriceRanges(String token, BigDecimal startPrice, BigDecimal endPrice) throws UnauthorizedAccessException;

    TripRequest addTripsRequestedByUser(String token, TripRequest tripRequest) throws UnauthorizedAccessException;

    //Both Admin and User operations
    List<TripLiteModel> getTripsByDestinationName(String token, String destinationName) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<TripLiteModel> getAllTrips(String token) throws UnauthorizedAccessException;

    TripModel getTripById(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException;

    List<String> suggestTrips(String query);

    void indexTrip(TripModel tripModel);

    List<Map<String, String>> getTripNamesById(String token, List<String> tripIds);

}
