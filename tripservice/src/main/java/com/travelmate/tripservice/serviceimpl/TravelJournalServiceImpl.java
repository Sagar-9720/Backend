package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.UserServiceClient;
import com.travelmate.tripservice.entity.TravelJournal;
import com.travelmate.tripservice.dto.TripInteractionDTO;
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
    private UserServiceClient userServiceClient;

    @Autowired
    private AuthServiceClient authServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(TravelJournalServiceImpl.class);

    @Override
    public TravelJournalModel createJournal(String token, TravelJournalModel journalModel) {
        if (token == null || token.isEmpty()) throw new AccessDeniedException("unauthorized access");
        TravelJournal entity = TravelJournalMapper.toEntity(journalModel);
        TravelJournal saved = travelJournalRepository.save(entity);
        return TravelJournalMapper.toModel(saved);
    }

    @Override
    public TravelJournalModel updateJournal(String token, TravelJournalModel journalModel) {
        if (token == null || token.isEmpty() || journalModel.getId() == null) return null;
        return travelJournalRepository.findById(journalModel.getId())
                .map(existing -> {
                    TravelJournal updated = TravelJournalMapper.toEntity(journalModel);
                    updated.setId(journalModel.getId());
                    TravelJournal saved = travelJournalRepository.save(updated);
                    return TravelJournalMapper.toModel(saved);
                })
                .orElse(null);
    }

    @Override
    public TravelJournalModel deleteJournal(String token, String id) {
        if (token == null || token.isEmpty() || id == null) return null;
        return travelJournalRepository.findById(id)
                .map(journal -> {
                    travelJournalRepository.deleteById(id);
                    return TravelJournalMapper.toModel(journal);
                })
                .orElse(null);
    }

    @Override
    public TravelJournalModel getJournalById(String token, String id) {
        if (token == null || token.isEmpty() || id == null) return null;
        return travelJournalRepository.findById(id)
                .map(TravelJournalMapper::toModel)
                .orElse(null);
    }

    @Override
    public List<TravelJournalModel> getJournalsByUserId(String token, String userId) {
        if (token == null || token.isEmpty() || userId == null) return List.of();
        return travelJournalRepository.findByUserId(userId).stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getJournalsByTripId(String token, String tripId) {
        if (token == null || token.isEmpty() || tripId == null) return List.of();
        return travelJournalRepository.findByTripId(tripId).stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getPublicJournals() {
        return travelJournalRepository.findByIsPublicTrue().stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> searchByTag(String tag) {
        if (tag == null || tag.isEmpty()) return List.of();
        return travelJournalRepository.findByTagsContaining(tag).stream().map(TravelJournalMapper::toModel).toList();
    }
}
