package com.travelmate.journalservice.config.cache;

import lombok.Data;
import java.io.Serializable;

@Data
public class CacheEvent implements Serializable {
    private String cacheName;
    private String key;
    private String operation; // CLEAR, UPDATE, DELETE
    private String instanceId;
    
    public CacheEvent(String cacheName, String key, String operation, String instanceId) {
        this.cacheName = cacheName;
        this.key = key;
        this.operation = operation;
        this.instanceId = instanceId;
    }
}
