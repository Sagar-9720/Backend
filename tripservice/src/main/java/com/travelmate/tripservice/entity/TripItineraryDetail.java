package com.travelmate.tripservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripItineraryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    private Trip trip;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private Itinerary itinerary;

    @Column(name = "day_number")
    @NotNull(message = "Day Number is required.")
    private Integer dayNumber;

    @Column(name = "arrival_time")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime departureTime;

    @ManyToMany
    @JoinTable(name = "trip_itinerary_detail_activity", joinColumns = @JoinColumn(name = "trip_itinerary_detail_id"), inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private Set<ItineraryActivity> activities;

}
