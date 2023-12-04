package com.example.caching.config;

public interface CacheManager {

    void addItemToCache(String key, String value, Long timout);

    String getItemFromCache(String key);
}
