package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByDestinationId(Long destinationId);
    // Add custom query methods if needed
}

