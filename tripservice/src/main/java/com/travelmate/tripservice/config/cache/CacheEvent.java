package com.travelmate.tripservice.config.cache;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheEvent implements Serializable {
    private String cacheName;
    private String key;
    private CacheOperation operation; // CLEAR, UPDATE, DELETE
    private String instanceId;

    public CacheEvent(String cacheName, String key, CacheOperation operation, String instanceId) {
        this.cacheName = cacheName;
        this.key = key;
        this.operation = operation;
        this.instanceId = instanceId;
    }

    public enum CacheOperation {
        CLEAR, UPDATE, DELETE
    }
}
