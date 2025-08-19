package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Destination;

import com.travelmate.tripservice.entity.Region;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    @EntityGraph(attributePaths = {"region", "region.country"})
    List<Destination> findByNameContainingIgnoreCase(String name);

    @EntityGraph(attributePaths = {"region", "region.country"})
    List<Destination> findByRegion(Region region);

    @EntityGraph(attributePaths = {"region", "region.country"})
    List<Destination> findByRegion_Country(Country country);
}

