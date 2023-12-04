package com.example.cachemanager;

public interface CacheManager {

  void addItemToCache(String key, String value, Long timout);

  String getItemFromCache(String key);
}
