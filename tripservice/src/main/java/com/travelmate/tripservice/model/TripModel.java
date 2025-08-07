package com.travelmate.tripservice.model;

import com.travelmate.tripservice.entity.Itinerary;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripModel {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private Long mainDestinationId;
    private List<Itinerary> itineraries; // List of Itinerary IDs for mapping
}
