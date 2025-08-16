package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Trip;
import com.travelmate.tripservice.entity.TripItineraryDetail;
import com.travelmate.tripservice.entity.TripRequest;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.entity.ItineraryActivity;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.model.TripLiteModel;
import com.travelmate.tripservice.model.TripModel;
import java.util.List;
import java.util.stream.Collectors;

public class RequestedTripMapper {
    public static Trip toTripEntity(TripRequest req) {
        Trip.TripBuilder tripBuilder = Trip.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .price(req.getPrice())
                .createdBy(req.getRequestedBy());

        // Map TripItineraryDetailRequest to TripItineraryDetail
        if (req.getItineraries() != null && !req.getItineraries().isEmpty()) {
            List<TripItineraryDetail> details = req.getItineraries().stream().map(detailReq -> {
                TripItineraryDetail.TripItineraryDetailBuilder detailBuilder = TripItineraryDetail.builder()
                        .dayNumber(detailReq.getDayNumber())
                        .arrivalTime(detailReq.getArrivalTime())
                        .departureTime(detailReq.getDepartureTime());

                // Map Itinerary
                TripRequest.RequestItinerary reqItinerary = detailReq.getRequestItinerary();
                if (reqItinerary != null) {
                    Itinerary.ItineraryBuilder itineraryBuilder = Itinerary.builder()
                            .itineraryName(reqItinerary.getName())
                            .description(reqItinerary.getDescription());
                    if (reqItinerary.getId() != null) {
                        itineraryBuilder.id(Long.valueOf(reqItinerary.getId()));
                    }
                    detailBuilder.itinerary(itineraryBuilder.build());
                }

                // Map activities
                if (detailReq.getActivities() != null && !detailReq.getActivities().isEmpty()) {
                    List<ItineraryActivity> activities = detailReq.getActivities().stream().map(act ->
                        ItineraryActivity.builder()
                            .activityName(act.getActivityName())
                            .description(act.getActivityDescription())
                            .build()
                    ).collect(Collectors.toList());
                    detailBuilder.activities(activities);
                }
                return detailBuilder.build();
            }).collect(Collectors.toList());
            tripBuilder.tripItineraryDetails(details);
        }
        return tripBuilder.build();
    }
}
