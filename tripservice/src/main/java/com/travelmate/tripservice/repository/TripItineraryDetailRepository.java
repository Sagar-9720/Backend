package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.TripItineraryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripItineraryDetailRepository extends JpaRepository<TripItineraryDetail, Long> {
}

