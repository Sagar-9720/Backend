package com.travelmate.journalservice.serviceimpl;

import com.travelmate.journalservice.entity.Tag;
import com.travelmate.journalservice.entity.TravelJournal;
import com.travelmate.journalservice.exceptions.TravelJournalNotFoundException;
import com.travelmate.journalservice.exceptions.UnauthorizedAccessException;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;
import com.travelmate.journalservice.mapper.TravelJournalMapper;
import com.travelmate.journalservice.repository.TagRepository;
import com.travelmate.journalservice.repository.TravelJournalRepository;
import com.travelmate.journalservice.service.TravelJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TravelJournalServiceImpl implements TravelJournalService {

    @Autowired
    private TravelJournalRepository travelJournalRepository;


    @Autowired
    private TagRepository tagRepository;

    private static final Logger logger = LoggerFactory.getLogger(TravelJournalServiceImpl.class);

    @Override
    public TravelJournalModel createJournal(TravelJournalModel journalModel) {
        if (journalModel == null) {
            throw new IllegalArgumentException("Journal model cannot be null");
        }
        if (journalModel.tags() != null) {
            for (String tag : journalModel.tags()) {
                if (tag != null && !tag.isBlank() && tagRepository.findByName(tag).isEmpty()) {
                    tagRepository.save(new Tag(0L, tag, 1L));
                    logger.info("Saved new tag: {}", tag);
                }
            }
        }
        TravelJournal saved = travelJournalRepository.save(TravelJournalMapper.toEntity(journalModel));
        logger.info("Saved Journal {}", saved);
        return TravelJournalMapper.toModel(saved);
    }

    @Override
    public TravelJournalModel updateJournal(String usedId, TravelJournalModel journalModel) {

        if (journalModel.tags() != null) {
            for (String tag : journalModel.tags()) {
                if (tag != null && !tag.isBlank() && tagRepository.findByName(tag).isEmpty()) {
                    tagRepository.save(new Tag(0L, tag, 1L));
                    logger.info("Saved new tag: {}", tag);
                }
            }
        }

        return travelJournalRepository.findById(journalModel.id()).map(existing -> {
            TravelJournal updated = TravelJournalMapper.toEntity(journalModel);
            updated.setId(journalModel.id());
            TravelJournal saved = travelJournalRepository.save(updated);
            logger.info("Saved Journal {}", saved);
            return TravelJournalMapper.toModel(saved);
        }).orElse(null);
    }

    @Override
    public TravelJournalModel deleteJournal(String userId, String id) {
        if (id == null || id.isEmpty()) {
            throw new TravelJournalNotFoundException(id);
        }
        TravelJournal travelJournal = travelJournalRepository.findById(id).orElse(null);
        if (travelJournal == null) {
            throw new TravelJournalNotFoundException(id);
        }
        if (!userId.equals(travelJournal.getUserId())) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        travelJournal.setDeletedAt(java.time.LocalDateTime.now());
        return TravelJournalMapper.toModel(travelJournal);
    }

    @Override
    public TravelJournalModel getJournalById(String id) {
        if (id == null || id.isEmpty()) {
            throw new TravelJournalNotFoundException(id);
        }
        logger.info("Fetching journal by id: {}", id);
        return travelJournalRepository.findById(id).map(TravelJournalMapper::toModel).orElse(null);
    }

    @Override
    public List<TravelJournalLiteModel> getJournalsByUserId(String role, String authUserId, String userId) {
        logger.info("Fetching journal by user id: {} as role: {}", userId, role);
        return switch (role != null ? role.toUpperCase() : "") {
            case "USER", "GUEST" -> {
                if (authUserId.equals(userId)) {
                    yield travelJournalRepository.findByUserId(userId).stream().filter(journal -> journal.getIsPublic() || journal.getUserId().equals(authUserId)).map(TravelJournalMapper::toLiteModel).toList();
                } else {
                    yield travelJournalRepository.findByUserId(userId).stream().filter(TravelJournal::getIsPublic).map(TravelJournalMapper::toLiteModel).toList();
                }
            }
            case "ADMIN", "SUBADMIN" ->
                    travelJournalRepository.findByUserId(userId).stream().map(TravelJournalMapper::toLiteModel).toList();
            default -> throw new UnauthorizedAccessException("unauthorized access");
        };
    }

    @Override
    public List<TravelJournalLiteModel> getJournalsByTripId(String userId, String role, String tripId) {
        logger.info("Fetching journal by user id: {} as role: {}", tripId, role);
        return switch (role != null ? role.toUpperCase() : "") {
            case "USER", "GUEST" ->
                    travelJournalRepository.findByTripId(tripId).stream().filter(journal -> journal.getIsPublic() || journal.getUserId().equals(userId)).map(TravelJournalMapper::toLiteModel).toList();

            case "ADMIN", "SUBADMIN" ->
                    travelJournalRepository.findByTripId(tripId).stream().map(TravelJournalMapper::toLiteModel).toList();
            default -> throw new UnauthorizedAccessException("unauthorized access");
        };
    }

    @Override
    public List<TravelJournalLiteModel> getPublicJournals() {
        return travelJournalRepository.findByIsPublicTrue().stream().map(TravelJournalMapper::toLiteModel).toList();
    }

    @Override
    public List<TravelJournalLiteModel> searchByTag(String userId, String tag) {
        return travelJournalRepository.findByTagsContaining(tag).stream().filter(t -> t.getUserId().equals(userId) || t.getIsPublic()).map(TravelJournalMapper::toLiteModel).toList();
    }

    @Override
    public List<TravelJournalLiteModel> getAllJournals(String role) {
        if (role == null || (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("subadmin"))) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        logger.info("Fetching all journals");
        return travelJournalRepository.findAll().stream().map(TravelJournalMapper::toLiteModel).toList();
    }
}
