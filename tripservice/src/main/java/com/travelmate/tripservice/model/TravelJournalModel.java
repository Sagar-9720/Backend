package com.travelmate.tripservice.model;

import com.travelmate.tripservice.entity.TravelJournal;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TravelJournalModel {
    private String id;
    private String userId;
    private String tripId;
    private String title;
    private String note;
    private LocalDateTime entryDate;
    private TravelJournal.Location location;
    private List<String> tags;
    private Boolean isPublic;
    private List<TravelJournal.ImageEntry> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

