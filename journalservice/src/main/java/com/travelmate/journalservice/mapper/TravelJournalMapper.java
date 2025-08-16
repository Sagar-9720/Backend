package com.travelmate.journalservice.mapper;

import com.travelmate.journalservice.entity.TravelJournal;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;

public class TravelJournalMapper {
    public static TravelJournalModel toModel(TravelJournal entity) {
        if (entity == null) return null;
        return new TravelJournalModel(entity.getId(), entity.getUserId(), entity.getTripId(), entity.getTitle(), entity.getNote(), entity.getEntryDate(), entity.getLocation(), entity.getCountry(), entity.getCity(), entity.getCategory(), entity.getSections(), entity.getTags(), entity.getIsPublic(), entity.getCreatedAt(), entity.getUpdatedAt(), entity.getDeletedAt());
    }

    public static TravelJournal toEntity(TravelJournalModel model) {
        if (model == null) return null;
        return TravelJournal.builder().id(model.id()).userId(model.userId()).tripId(model.tripId()).title(model.title()).note(model.note()).entryDate(model.entryDate()).location(model.location()).country(model.country()).city(model.city()).category(model.category()).sections(model.sections()).tags(model.tags()).isPublic(model.isPublic()).createdAt(model.createdAt()).updatedAt(model.updatedAt()).deletedAt(model.deletedAt()).build();
    }

    public static TravelJournalLiteModel toLiteModel(TravelJournal entity) {
        if (entity == null) return null;
        return new TravelJournalLiteModel(entity.getId(), entity.getUserId(), entity.getTripId(), entity.getTitle(), entity.getIsPublic(), entity.getUpdatedAt());
    }
}
