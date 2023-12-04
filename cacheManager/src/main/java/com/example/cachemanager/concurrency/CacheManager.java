package com.example.cachemanager.concurrency;

import java.util.List;

public interface CacheManager {

  String addItemToCache(String requestKey, Long timout);

  void deleteFromCache(String key);
  boolean searchItemInCache(String key);

  List<String> checkConcurrency(String... keys);

}
