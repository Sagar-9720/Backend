package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.domain.TravelJournal;
import com.travelmate.tripservice.model.TravelJournalModel;
import com.travelmate.tripservice.mapper.TravelJournalMapper;
import com.travelmate.tripservice.repository.TravelJournalRepository;
import com.travelmate.tripservice.service.TravelJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TravelJournalServiceImpl implements TravelJournalService {
    @Autowired
    private TravelJournalRepository travelJournalRepository;

    private static final Logger logger = LoggerFactory.getLogger(TravelJournalServiceImpl.class);

    @Override
    public TravelJournalModel createJournal(TravelJournalModel journalModel) {
        logger.info("Creating travel journal for user: {}", journalModel.getUserId());
        TravelJournal entity = TravelJournalMapper.toEntity(journalModel);
        TravelJournal saved = travelJournalRepository.save(entity);
        return TravelJournalMapper.toModel(saved);
    }

    @Override
    public TravelJournalModel updateJournal(String id, TravelJournalModel journalModel) {
        logger.info("Updating travel journal id: {}", id);
        TravelJournal entity = TravelJournalMapper.toEntity(journalModel);
        entity.setId(id);
        TravelJournal saved = travelJournalRepository.save(entity);
        return TravelJournalMapper.toModel(saved);
    }

    @Override
    public void deleteJournal(String id) {
        logger.info("Deleting travel journal id: {}", id);
        travelJournalRepository.deleteById(id);
    }

    @Override
    public TravelJournalModel getJournalById(String id) {
        logger.info("Fetching travel journal by id: {}", id);
        return travelJournalRepository.findById(id).map(TravelJournalMapper::toModel).orElse(null);
    }

    @Override
    public List<TravelJournalModel> getJournalsByUserId(String userId) {
        logger.info("Fetching travel journals by user id: {}", userId);
        return travelJournalRepository.findByUserId(userId).stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getJournalsByTripId(String tripId) {
        logger.info("Fetching travel journals by trip id: {}", tripId);
        return travelJournalRepository.findByTripId(tripId).stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> getPublicJournals() {
        logger.info("Fetching public travel journals");
        return travelJournalRepository.findByIsPublicTrue().stream().map(TravelJournalMapper::toModel).toList();
    }

    @Override
    public List<TravelJournalModel> searchByTag(String tag) {
        logger.info("Searching travel journals by tag: {}", tag);
        return travelJournalRepository.findByTagsContaining(tag).stream().map(TravelJournalMapper::toModel).toList();
    }
}
