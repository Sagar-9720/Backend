package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.TagModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/trip/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<TagModel>>> getAllTags() {
        List<TagModel> tags = tagService.getAllTags();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tags fetched", tags, "/tags"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<TagModel>> getTagById(@PathVariable Long id) {
        Optional<TagModel> tag = tagService.getTagById(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tag fetched", tag.orElse(null), "/tags/" + id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CustomResponseEntity<TagModel>> getTagByName(@PathVariable String name) {
        Optional<TagModel> tag = tagService.getTagByName(name);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tag fetched by name", tag.orElse(null), "/tags/name/" + name));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<TagModel>> saveTag(@RequestBody TagModel tagModel) {
        TagModel saved = tagService.saveTag(tagModel);
        return ResponseEntity.ok(CustomResponseEntity.success(201, "Tag created", saved, "/tags"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Tag deleted", null, "/tags/" + id));
    }
}
