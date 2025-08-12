package com.travelmate.tripservice.service;

import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.model.TripModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripService {
    TripModel createTrip(String token, TripModel tripModel) throws TripExistsException, UnauthorizedAccessException;

    TripModel getTripById(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException;

    List<TripModel> getAllTrips(String token) throws UnauthorizedAccessException;

    TripModel updateTrip(String token, TripModel updatedTripModel) throws TripNotFoundException, UnauthorizedAccessException;

    TripModel deleteTrip(String token, Long id) throws TripNotFoundException, UnauthorizedAccessException;

    List<TripModel> getTripsByDestinationName(String token,String destinationName) throws DestinationNotFoundException, UnauthorizedAccessException;

    List<TripModel> tripsBtwPriceRanges(String token, BigDecimal startPrice, BigDecimal endPrice) throws UnauthorizedAccessException;

    void autoDeleteTripByDate(String token) throws UnauthorizedAccessException;

    List<TripModel> getTripRequestByUserId(String token, String userId) throws UnauthorizedAccessException;

    List<TripModel> addTripsRequestedByUser(String token, TripModel tripModel) throws UnauthorizedAccessException;

    TripModel approveTripRequest(String token, String tripRequestId, TripRequest tripRequest) throws UnauthorizedAccessException;

    List<TripModel> getAllTripsRequested(String token) throws UnauthorizedAccessException;
}
