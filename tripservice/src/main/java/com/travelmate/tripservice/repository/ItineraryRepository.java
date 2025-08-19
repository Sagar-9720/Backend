package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.Itinerary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    @EntityGraph(attributePaths = {"destination", "destination.region", "destination.region.country"})
    List<Itinerary> findByDestinationId(Long destinationId);

    @EntityGraph(attributePaths = {"destination", "destination.region", "destination.region.country"})
    List<Itinerary> findAll();

    @EntityGraph(attributePaths = {"destination", "destination.region", "destination.region.country"})
    java.util.Optional<Itinerary> findById(Long id);
}
