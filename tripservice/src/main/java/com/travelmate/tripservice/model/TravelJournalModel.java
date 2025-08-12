package com.travelmate.tripservice.model;

import com.travelmate.tripservice.entity.TravelJournal;

import java.time.LocalDateTime;
import java.util.List;

public record TravelJournalModel(String id, String userId, String tripId, String title, String note,
                                 LocalDateTime entryDate, TravelJournal.Location location, List<String> tags,
                                 Boolean isPublic, List<TravelJournal.ImageEntry> images, LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
}
