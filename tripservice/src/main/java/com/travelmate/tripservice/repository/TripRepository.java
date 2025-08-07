package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Search trips by title containing keyword (case-insensitive)
    List<Trip> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT t FROM Trip t WHERE LOWER(t.mainDestination.name) LIKE LOWER(CONCAT('%', :mainDestination, '%'))")
    List<Trip> findByMainDestinationContainingIgnoreCase(@Param("mainDestination") String mainDestination);


    // Get trips within a price range
    List<Trip> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice);

    // (Optional) Get trips that are starting after today (upcoming trips)
    @Query("SELECT t FROM Trip t WHERE t.startDate > CURRENT_DATE")
    List<Trip> findUpcomingTrips();

    // (Optional) Get trips sorted by popularity (assumes a 'popularity' field exists)
//    @Query("SELECT t FROM Trip t ORDER BY t.popularity DESC")
//    List<Trip> findTopPopularTrips();
}
