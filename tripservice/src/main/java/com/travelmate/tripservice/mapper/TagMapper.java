package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Tag;
import com.travelmate.tripservice.model.TagModel;

public class TagMapper {
    public static TagModel toModel(Tag entity) {
        if (entity == null) return null;
        return new TagModel(entity.getId(), entity.getName(), entity.getUsageCount());
    }

    public static Tag toEntity(TagModel model) {
        if (model == null) return null;
        return Tag.builder().id(model.id()).name(model.name()).usageCount(model.usageCount()).build();
    }
}
