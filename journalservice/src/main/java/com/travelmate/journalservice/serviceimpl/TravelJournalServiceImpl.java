package com.travelmate.journalservice.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.journalservice.client.AuthServiceClient;
import com.travelmate.journalservice.client.TokenValidationResponse;
import com.travelmate.journalservice.entity.Tag;
import com.travelmate.journalservice.entity.TravelJournal;
import com.travelmate.journalservice.exceptions.TravelJournalNotFoundException;
import com.travelmate.journalservice.exceptions.UnauthorizedAccessException;
import com.travelmate.journalservice.model.TravelJournalLiteModel;
import com.travelmate.journalservice.model.TravelJournalModel;
import com.travelmate.journalservice.mapper.TravelJournalMapper;
import com.travelmate.journalservice.repository.TagRepository;
import com.travelmate.journalservice.repository.TravelJournalRepository;
import com.travelmate.journalservice.service.TokenValidationService;
import com.travelmate.journalservice.service.TravelJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TravelJournalServiceImpl implements TravelJournalService {

    @Autowired
    private TravelJournalRepository travelJournalRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TokenValidationService tokenValidationService;

    private static final Logger logger = LoggerFactory.getLogger(TravelJournalServiceImpl.class);

    @Override
    public TravelJournalModel createJournal(String token, TravelJournalModel journalModel) {
        if (token == null || token.isEmpty()) {
            throw new AccessDeniedException("unauthorized access");
        }
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
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
    public TravelJournalModel updateJournal(String token, TravelJournalModel journalModel) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        String userId = tokenValidationService.getUserId(token);
        if (!journalModel.userId().equals(userId)) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        // Persist new tags if not already present
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
    public TravelJournalModel deleteJournal(String token, String id) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }

        if (id == null || id.isEmpty()) {
            throw new TravelJournalNotFoundException(id);
        }
        String userId = tokenValidationService.getUserId(token);

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
    public TravelJournalModel getJournalById(String token, String id) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (id == null || id.isEmpty()) {
            throw new TravelJournalNotFoundException(id);
        }
        logger.info("Fetching journal by id: {}", id);
        return travelJournalRepository.findById(id).map(TravelJournalMapper::toModel).orElse(null);
    }

    @Override
    public List<TravelJournalLiteModel> getJournalsByUserId(String token, String userId) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        String requesterUserId = tokenValidationService.getUserId(token);
        String role = tokenValidationService.getRole(token);

        logger.info("Fetching journal by user id: {} as role: {}", userId, role);
        return switch (role != null ? role.toUpperCase() : "") {
            case "USER", "GUEST" -> {
                if (requesterUserId.equals(userId)) {
                    yield travelJournalRepository.findByUserId(userId).stream().filter(journal -> journal.getIsPublic() || journal.getUserId().equals(requesterUserId)).map(TravelJournalMapper::toLiteModel).toList();
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
    public List<TravelJournalLiteModel> getJournalsByTripId(String token, String tripId) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        String userId = tokenValidationService.getUserId(token);
        String role = tokenValidationService.getRole(token);

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
    public List<TravelJournalLiteModel> getPublicJournals(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        return travelJournalRepository.findByIsPublicTrue().stream().map(TravelJournalMapper::toLiteModel).toList();
    }

    @Override
    public List<TravelJournalLiteModel> searchByTag(String token, String tag) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        String userId = tokenValidationService.getUserId(token);
        if (tag == null || tag.isEmpty()) {
            return List.of();
        }
        return travelJournalRepository.findByTagsContaining(tag).stream().filter(t -> t.getUserId().equals(userId) || t.getIsPublic()).map(TravelJournalMapper::toLiteModel).toList();
    }

    @Override
    public List<TravelJournalLiteModel> getAllJournals(String token) throws JsonProcessingException {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        String role = tokenValidationService.getRole(token);
        if (role == null || (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("subadmin"))) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        logger.info("Fetching all journals");
        return travelJournalRepository.findAll().stream().map(TravelJournalMapper::toLiteModel).toList();
    }
}
