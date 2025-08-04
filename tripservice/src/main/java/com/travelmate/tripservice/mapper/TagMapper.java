package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.domain.Tag;
import com.travelmate.tripservice.model.TagModel;

public class TagMapper {
    public static TagModel toModel(Tag entity) {
        if (entity == null) return null;
        return TagModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .usageCount(entity.getUsageCount())
                .build();
    }

    public static Tag toEntity(TagModel model) {
        if (model == null) return null;
        return Tag.builder()
                .id(model.getId())
                .name(model.getName())
                .usageCount(model.getUsageCount())
                .build();
    }
}

