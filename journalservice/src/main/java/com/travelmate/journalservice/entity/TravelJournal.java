package com.travelmate.journalservice.entity;

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

    // Core Info
    private String userId;
    private String tripId;
    private String title;
    private String note; // Introductory note / summary
    private Boolean isPublic;

    // Location Metadata
    private Location location;
    private String country;
    private String city;
    private String category; // Adventure, Relaxation, Cultural, etc.

    // Content
    private List<JournalSection> sections; // Day-by-day or thematic sections
    private List<String> tags; // For search/discovery


    // Timestamps
    private LocalDateTime entryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt; // Soft delete support


    // ---------------- Nested Classes ----------------

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
    public static class MediaEntry {
        private String url;
        private String caption;
        private MediaType type; // IMAGE, VIDEO, AUDIO, DOCUMENT
        private LocalDateTime uploadedAt;
    }

    public enum MediaType {
        IMAGE, VIDEO, AUDIO, DOCUMENT
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JournalSection {
        private String dayTitle; // e.g., "Day 3 - Exploring Kyoto"
        private String content;
        private List<MediaEntry> media;
    }

}
