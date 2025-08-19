package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.TripItineraryDetail;
import com.travelmate.tripservice.mapper.TripItineraryDetailMapper;
import com.travelmate.tripservice.model.TripItineraryDetailModel;
import com.travelmate.tripservice.repository.TripItineraryDetailRepository;
import com.travelmate.tripservice.service.TripItineraryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripItineraryDetailServiceImpl implements TripItineraryDetailService {


    @Autowired
    private TripItineraryDetailRepository repository;


    @Override
    public TripItineraryDetailModel create(TripItineraryDetailModel model) {
        TripItineraryDetail entity = TripItineraryDetailMapper.toEntity(model);
        return TripItineraryDetailMapper.toModel(repository.save(entity));
    }

    @Override
    public TripItineraryDetailModel update(Long id, TripItineraryDetailModel model) {
        TripItineraryDetail entity = repository.findById(id).orElseThrow(() -> new RuntimeException("TripItineraryDetail not found"));

        entity.setDayNumber(model.dayNumber());
        entity.setArrivalTime(model.arrivalTime());
        entity.setDepartureTime(model.departureTime());

        return TripItineraryDetailMapper.toModel(repository.save(entity));
    }

    @Override
    public TripItineraryDetailModel getById(Long id) {
        return repository.findById(id).map(TripItineraryDetailMapper::toModel).orElseThrow();
    }

    @Override
    public List<TripItineraryDetailModel> getAll() {
        return repository.findAll().stream().map(TripItineraryDetailMapper::toModel).toList();
    }
}
