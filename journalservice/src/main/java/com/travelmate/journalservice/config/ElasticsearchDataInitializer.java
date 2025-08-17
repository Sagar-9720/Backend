package com.travelmate.journalservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.travelmate.journalservice.entity.Tag;
import com.travelmate.journalservice.entity.TravelJournal;
import com.travelmate.journalservice.repository.TagRepository;
import com.travelmate.journalservice.repository.TravelJournalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
class ElasticsearchDataInitializer {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private TravelJournalRepository travelJournalRepository;

    @Autowired
    private TagRepository tagRepository;

    @PostConstruct
    public void init() {
        try {
            // Journals
            if (!indexExists("journals")) {
                elasticsearchClient.indices().create(new CreateIndexRequest.Builder().index("journals").build());
            }
            List<TravelJournal> journals = travelJournalRepository.findAll();
            for (TravelJournal journal : journals) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("id", journal.getId());
                doc.put("title", journal.getTitle());
                elasticsearchClient.index(IndexRequest.of(i -> i.index("journals").id(String.valueOf(journal.getId())).document(doc)));
            }
            // Tags
            if (!indexExists("tags")) {
                elasticsearchClient.indices().create(new CreateIndexRequest.Builder().index("tags").build());
            }
            List<Tag> tags = tagRepository.findAll();
            for (Tag tag : tags) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("id", tag.getId());
                doc.put("name", tag.getName());
                elasticsearchClient.index(IndexRequest.of(i -> i.index("tags").id(String.valueOf(tag.getId())).document(doc)));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean indexExists(String indexName) throws Exception {
        return elasticsearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value(); // ✅ returns boolean
    }


}
