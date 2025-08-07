package com.travelmate.tripservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "travel_journals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelJournal {

    @Id
    private String id;

    private String userId;

    private String tripId;

    private String title;

    private String note;

    private LocalDateTime entryDate;

    private Location location;

    private List<String> tags;

    private Boolean isPublic;

    private List<ImageEntry> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        private Double lat;
        private Double lng;
        private String placeName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageEntry {
        private String url;
        private String caption;
    }
}
