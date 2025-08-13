package com.travelmate.tripservice.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TripModel(Long id, String title, String description,
                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                        @JsonSerialize(using = LocalDateTimeSerializer.class)
                        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                        LocalDateTime startDate,
                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                        @JsonSerialize(using = LocalDateTimeSerializer.class)
                        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                        LocalDateTime endDate,
                        BigDecimal price, Long mainDestinationId, String createdBy, List<ItineraryModel> itineraries) {
}
