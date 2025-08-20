package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Search trips by title containing keyword (case-insensitive)
    List<Trip> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(attributePaths = {"mainDestination"
            , "mainDestination.region"
            , "mainDestination.region.country"
            , "tripItineraryDetails"
            , "tripItineraryDetails.itinerary"
            , "tripItineraryDetails.itinerary.destination"
            , "tripItineraryDetails.itinerary.destination.region"
            , "tripItineraryDetails.itinerary.destination.region.country"
            , "tripItineraryDetails.activities"
    })
    @Query("SELECT t FROM Trip t WHERE LOWER(t.mainDestination.name) LIKE LOWER(CONCAT('%', :mainDestination, '%'))")
    List<Trip> findByMainDestinationContainingIgnoreCase(@Param("mainDestination") String mainDestination);

    @EntityGraph(attributePaths = {"mainDestination"
            , "mainDestination.region"
            , "mainDestination.region.country"
            , "tripItineraryDetails"
            , "tripItineraryDetails.itinerary"
            , "tripItineraryDetails.itinerary.destination"
            , "tripItineraryDetails.itinerary.destination.region"
            , "tripItineraryDetails.itinerary.destination.region.country"
            , "tripItineraryDetails.activities"
    })
    @Query("SELECT t FROM Trip t WHERE t.price BETWEEN :startPrice AND :endPrice")
    List<Trip> findByPriceBetween(@Param("startPrice") BigDecimal startPrice, @Param("endPrice") BigDecimal endPrice);

    @EntityGraph(attributePaths = {"mainDestination"
            , "mainDestination.region"
            , "mainDestination.region.country"
            , "tripItineraryDetails"
            , "tripItineraryDetails.itinerary"
            , "tripItineraryDetails.itinerary.destination"
            , "tripItineraryDetails.itinerary.destination.region"
            , "tripItineraryDetails.itinerary.destination.region.country"
            , "tripItineraryDetails.activities"
    })
    List<Trip> findAll();

    @EntityGraph(attributePaths = {"mainDestination"
            , "mainDestination.region"
            , "mainDestination.region.country"
            , "tripItineraryDetails"
            , "tripItineraryDetails.itinerary"
            , "tripItineraryDetails.itinerary.destination"
            , "tripItineraryDetails.itinerary.destination.region"
            , "tripItineraryDetails.itinerary.destination.region.country"
            , "tripItineraryDetails.activities"
    })
    Optional<Trip> findById(Long id);


}
