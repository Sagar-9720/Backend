package com.travelmate.tripservice.repository;

import com.travelmate.tripservice.entity.TripRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRequestRepository extends MongoRepository<TripRequest, String> {
    List<TripRequest> findByApprovedFalse();
    List<TripRequest> findByRequestedBy(String requestedBy);
}

