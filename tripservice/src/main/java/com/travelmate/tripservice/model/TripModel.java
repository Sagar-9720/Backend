package com.travelmate.tripservice.model;


import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TripModel(Long id, String title, String description, LocalDateTime startDate, LocalDateTime endDate,
                        BigDecimal price, Long mainDestinationId, String createdBy, List<ItineraryModel> itineraries) {
}
