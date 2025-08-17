package com.travelmate.journalservice.controller;

import com.travelmate.journalservice.model.TagModel;
import com.travelmate.journalservice.response.CustomResponseEntity;
import com.travelmate.journalservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/journal/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TagModel>>> getAllTags() {
        List<TagModel> tags = tagService.getAllTags();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tags fetched", tags, "/tags"));
    }

    @GetMapping("/suggest")
    public ResponseEntity<CustomResponseEntity<List<String>>> suggestTags(@RequestParam("q") String query) {
        List<String> suggestions = tagService.suggestTags(query);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tag suggestions fetched", suggestions, "/tags/suggest?q=" + query));
    }
}
