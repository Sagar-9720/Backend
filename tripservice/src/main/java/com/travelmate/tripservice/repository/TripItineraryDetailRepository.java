package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.TripItineraryDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripItineraryDetailRepository extends JpaRepository<TripItineraryDetail, Long> {

    @EntityGraph(attributePaths = {"itinerary", "itinerary.destination", "itinerary.destination.region", "itinerary.destination.country", "activities"})
    List<TripItineraryDetail> findAll();
}

