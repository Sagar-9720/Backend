package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.UserServiceClient;
import com.travelmate.tripservice.entity.TravelJournal;
import com.travelmate.tripservice.dto.TripInteractionDTO;
import com.travelmate.tripservice.exceptions.TravelJournalNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.TravelJournalModel;
import com.travelmate.tripservice.mapper.TravelJournalMapper;
import com.travelmate.tripservice.repository.TravelJournalRepository;
import com.travelmate.tripservice.service.TravelJournalService;
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

    private static final Logger logger = LoggerFactory.getLogger(TravelJournalServiceImpl.class);

    @Override
    public TravelJournalModel createJournal(String token, TravelJournalModel journalModel) {
        if (token == null || token.isEmpty()) throw new AccessDeniedException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new AccessDeniedException("unauthorized access");
        }

        TravelJournal saved = travelJournalRepository.save(TravelJournalMapper.toEntity(journalModel));
        logger.info("Saved Journal {}", saved);
        return TravelJournalMapper.toModel(saved);
    }

    @Override
    public TravelJournalModel updateJournal(String token, TravelJournalModel journalModel) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
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
    public TravelJournalModel deleteJournal(String token, String id) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (id == null || id.isEmpty()) throw new TravelJournalNotFoundException(id);

        return travelJournalRepository.findById(id).map(journal -> {
            travelJournalRepository.deleteById(id);
            logger.info("Deleted Journal with id {}", id);
            return TravelJournalMapper.toModel(journal);
        }).orElse(null);
    }

    @Override
    public TravelJournalModel getJournalById(String token, String id) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        if (id == null || id.isEmpty()) throw new TravelJournalNotFoundException(id);
        logger.info("Fetching journal by id: {}", id);
        return travelJournalRepository.findById(id).map(TravelJournalMapper::toModel).orElse(null);
    }

    @Override
    public List<TravelJournalModel> getJournalsByUserId(String token, String userId) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        logger.info("Fetching journal by user id: {}", userId);
        return travelJournalRepository.findByUserId(userId).stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getJournalsByTripId(String token, String tripId) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        return travelJournalRepository.findByTripId(tripId).stream().filter(tripJournal -> tripJournal.getIsPublic() == true).map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getPublicJournals(String token) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        return travelJournalRepository.findByIsPublicTrue().stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> searchByTag(String token, String tag) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        if (!authServiceClient.validateToken(token).isValid()) {
            throw new UnauthorizedAccessException("unauthorized access");

        }
        if (tag == null || tag.isEmpty()) return List.of();
        return travelJournalRepository.findByTagsContaining(tag).stream().filter(travelJournal -> travelJournal.getIsPublic() == true).map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getAllJournals(String token) {
        if (token == null || token.isEmpty()) throw new UnauthorizedAccessException("unauthorized access");
        String role = authServiceClient.validateToken(token).getRole();
        if (role == null || (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("subadmin"))) {
            throw new UnauthorizedAccessException("unauthorized access");
        }
        logger.info("Fetching all journals");
        return travelJournalRepository.findAll().stream().map(TravelJournalMapper::toModel).toList();
    }
}
