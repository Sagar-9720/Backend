package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.domain.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    // Add custom query methods if needed
}

