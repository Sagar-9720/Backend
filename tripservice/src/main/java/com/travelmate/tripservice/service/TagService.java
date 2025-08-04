package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.TagModel;

import java.util.List;
import java.util.Optional;

public interface TagService {
    TagModel saveTag(TagModel tagModel);
    Optional<TagModel> getTagById(Long id);
    Optional<TagModel> getTagByName(String name);
    List<TagModel> getAllTags();
    void deleteTag(Long id);
}
