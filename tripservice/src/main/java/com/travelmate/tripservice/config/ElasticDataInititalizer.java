package com.travelmate.tripservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.TripRepository;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Trip;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;

@Component
class ElasticsearchDataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchDataInitializer.class);

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private TripRepository tripRepository;

    @PostConstruct
    public void init() {
        try {
            // Destinations
            if (!indexExists("destinations")) {
                elasticsearchClient.indices().create(new CreateIndexRequest.Builder().index("destinations").build());
            }
            List<Destination> destinations = destinationRepository.findAll();
            for (Destination dest : destinations) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("id", dest.getId());
                doc.put("name", dest.getName());
                elasticsearchClient.index(IndexRequest.of(i -> i.index("destinations").id(String.valueOf(dest.getId())).document(doc)));
            }
            // Trips
            if (!indexExists("trips")) {
                elasticsearchClient.indices().create(new CreateIndexRequest.Builder().index("trips").build());
            }
            List<Trip> trips = tripRepository.findAll();
            for (Trip trip : trips) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("title", trip.getTitle());
                doc.put("description", trip.getDescription());
                elasticsearchClient.index(IndexRequest.of(i -> i.index("trips").id(String.valueOf(trip.getId())).document(doc)));
            }
        } catch (Exception e) {
            logger.error("Error initializing Elasticsearch data", e);
        }
    }

    private boolean indexExists(String indexName) throws Exception {
        return elasticsearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value(); // ✅ returns boolean
    }


}
