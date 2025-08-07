package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Tag;
import com.travelmate.tripservice.repository.TagRepository;
import com.travelmate.tripservice.service.TagService;
import com.travelmate.tripservice.model.TagModel;
import com.travelmate.tripservice.mapper.TagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Override
    public TagModel saveTag(TagModel tagModel) {
        logger.info("Saving tag: {}", tagModel.getName());
        Tag tag = TagMapper.toEntity(tagModel);
        Tag saved = tagRepository.save(tag);
        return TagMapper.toModel(saved);
    }

    @Override
    public Optional<TagModel> getTagById(Long id) {
        logger.info("Fetching tag by id: {}", id);
        return tagRepository.findById(id).map(TagMapper::toModel);
    }

    @Override
    public Optional<TagModel> getTagByName(String name) {
        logger.info("Fetching tag by name: {}", name);
        return tagRepository.findByName(name).map(TagMapper::toModel);
    }

    @Override
    public List<TagModel> getAllTags() {
        logger.info("Fetching all tags");
        return tagRepository.findAll().stream().map(TagMapper::toModel).toList();
    }

    @Override
    public void deleteTag(Long id) {
        logger.info("Deleting tag id: {}", id);
        try {
            tagRepository.deleteById(id);
        } catch (RuntimeException e) {
            logger.warn("Tag not found for deletion: {}", id);

        }
    }
}
