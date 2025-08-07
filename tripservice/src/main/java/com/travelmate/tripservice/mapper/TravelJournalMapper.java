package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.TravelJournal;
import com.travelmate.tripservice.model.TravelJournalModel;

import java.util.stream.Collectors;

public class TravelJournalMapper {
    public static TravelJournalModel toModel(TravelJournal entity) {
        if (entity == null) return null;
        return TravelJournalModel.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tripId(entity.getTripId())
                .title(entity.getTitle())
                .note(entity.getNote())
                .entryDate(entity.getEntryDate())
                .location(entity.getLocation())
                .images(entity.getImages())
                .tags(entity.getTags())
                .isPublic(entity.getIsPublic())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TravelJournal toEntity(TravelJournalModel model) {
        if (model == null) return null;
        return TravelJournal.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .tripId(model.getTripId())
                .title(model.getTitle())
                .note(model.getNote())
                .entryDate(model.getEntryDate())
                .tags(model.getTags())
                .location(model.getLocation())
                .images(model.getImages())
                .isPublic(model.getIsPublic())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
