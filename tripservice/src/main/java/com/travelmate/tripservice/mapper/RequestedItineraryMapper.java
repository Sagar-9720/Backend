package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.entity.TripRequest;

public class RequestedItineraryMapper {
    public static TripRequest.RequestedItinerary toRequestedItinerary(Itinerary itinerary) {
        if (itinerary == null) return null;
        return TripRequest.RequestedItinerary.builder().id(itinerary.getId() != null ? itinerary.getId().toString() : null).itineraryName(itinerary.getItineraryName()).itineraryDescription(itinerary.getDescription()).mainDestination(itinerary.getDestination()).arrivalTime(itinerary.getArrivalTime()).departureTime(itinerary.getDepartureTime()).build();
    }

    public static Itinerary toItinerary(TripRequest.RequestedItinerary req) {
        if (req == null) return null;
        Itinerary itinerary = new Itinerary();
        if (req.getId() != null) {
            try {
                itinerary.setId(Long.valueOf(req.getId()));
            } catch (NumberFormatException ignored) {
            }
        }
        itinerary.setItineraryName(req.getItineraryName());
        itinerary.setDescription(req.getItineraryDescription());
        itinerary.setDestination(req.getMainDestination());
        itinerary.setArrivalTime(req.getArrivalTime());
        itinerary.setDepartureTime(req.getDepartureTime());
        return itinerary;
    }


}

