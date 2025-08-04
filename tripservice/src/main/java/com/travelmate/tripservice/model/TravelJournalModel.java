package com.travelmate.tripservice.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelJournalModel {
    private String id;
    private String userId;
    private String tripId;
    private String title;
    private String note;
    private LocalDateTime entryDate;
    private String location; // Simplified for DTO
    private List<String> tags;
    private Boolean isPublic;
    private List<String> images; // Simplified for DTO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

