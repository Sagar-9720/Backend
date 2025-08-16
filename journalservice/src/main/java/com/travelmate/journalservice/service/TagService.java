package com.travelmate.journalservice.service;

import com.travelmate.journalservice.model.TagModel;

import java.util.List;

public interface TagService {

    List<TagModel> getAllTags();

    List<String> suggestTags(String query);

    void indexTag(TagModel tagModel);
}
