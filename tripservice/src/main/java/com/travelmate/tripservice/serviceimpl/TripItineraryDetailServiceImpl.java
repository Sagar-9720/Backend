package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.ItineraryActivity;
import com.travelmate.tripservice.entity.TripItineraryDetail;
import com.travelmate.tripservice.mapper.TripItineraryDetailMapper;
import com.travelmate.tripservice.model.TripItineraryDetailModel;
import com.travelmate.tripservice.repository.ItineraryActivityRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.repository.TripItineraryDetailRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.service.TripItineraryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TripItineraryDetailServiceImpl implements TripItineraryDetailService {

    @Autowired
    private TripItineraryDetailRepository repository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private ItineraryActivityRepository activityRepository;

    @Override
    public TripItineraryDetailModel create(TripItineraryDetailModel model) {
        TripItineraryDetail entity = new TripItineraryDetail();

        // Only map basic info; trip and itinerary persistence comes from TripService
        entity.setDayNumber(model.dayNumber());
        entity.setArrivalTime(model.arrivalTime());
        entity.setDepartureTime(model.departureTime());

        // Map activities if present
        if (model.activities() != null && !model.activities().isEmpty()) {
            Set<ItineraryActivity> activities = model.activities().stream().map(act -> activityRepository.findById(act.id()).orElseThrow(() -> new RuntimeException("ItineraryActivity not found with id: " + act.id()))).collect(Collectors.toSet());
            entity.setActivities(activities);
        }

        TripItineraryDetail saved = repository.save(entity);
        return TripItineraryDetailMapper.toModel(saved);
    }

    @Override
    public TripItineraryDetailModel update(Long id, TripItineraryDetailModel model) {
        TripItineraryDetail entity = repository.findById(id).orElseThrow(() -> new RuntimeException("TripItineraryDetail not found with id: " + id));

        entity.setDayNumber(model.dayNumber());
        entity.setArrivalTime(model.arrivalTime());
        entity.setDepartureTime(model.departureTime());

        // Update activities
        if (model.activities() != null) {
            Set<ItineraryActivity> activities = model.activities().stream().map(act -> activityRepository.findById(act.id()).orElseThrow(() -> new RuntimeException("ItineraryActivity not found with id: " + act.id()))).collect(Collectors.toSet());
            entity.setActivities(activities);
        }

        TripItineraryDetail saved = repository.save(entity);
        return TripItineraryDetailMapper.toModel(saved);
    }

    @Override
    public TripItineraryDetailModel getById(Long id) {
        return repository.findById(id).map(TripItineraryDetailMapper::toModel).orElseThrow(() -> new RuntimeException("TripItineraryDetail not found with id: " + id));
    }

    @Override
    public List<TripItineraryDetailModel> getAll() {
        return repository.findAll().stream().map(TripItineraryDetailMapper::toModel).toList();
    }
}
