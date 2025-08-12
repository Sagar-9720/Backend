package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.TravelJournal;
import com.travelmate.tripservice.model.TravelJournalModel;

public class TravelJournalMapper {
    public static TravelJournalModel toModel(TravelJournal entity) {
        if (entity == null) return null;
        return new TravelJournalModel(
                entity.getId(),
                entity.getUserId(),
                entity.getTripId(),
                entity.getTitle(),
                entity.getNote(),
                entity.getEntryDate(),
                entity.getLocation(),
                entity.getTags(),
                entity.getIsPublic(),
                entity.getImages(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static TravelJournal toEntity(TravelJournalModel model) {
        if (model == null) return null;
        return TravelJournal.builder()
                .id(model.id())
                .userId(model.userId())
                .tripId(model.tripId())
                .title(model.title())
                .note(model.note())
                .entryDate(model.entryDate())
                .tags(model.tags())
                .location(model.location())
                .images(model.images())
                .isPublic(model.isPublic())
                .createdAt(model.createdAt())
                .updatedAt(model.updatedAt())
                .build();
    }
}
