package com.travelmate.journalservice.mapper;

import com.travelmate.journalservice.entity.Tag;
import com.travelmate.journalservice.model.TagModel;

public class TagMapper {
    public static TagModel toModel(Tag entity) {
        if (entity == null) return null;
        return new TagModel(entity.getId(), entity.getName(), entity.getUsageCount());
    }
}
