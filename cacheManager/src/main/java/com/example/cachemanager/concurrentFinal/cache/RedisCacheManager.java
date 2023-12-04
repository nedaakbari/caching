package com.example.cachemanager.concurrentFinal.cache;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisCacheManager implements CacheManager {

  private final RedissonClient redissonClient;

  @Autowired
  public RedisCacheManager(RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
  }

  public String addItemToCache(String requestKey, Long timout) {
    if (requestKey == null) {
      return null;
    }
    RBucket<String> rBucket = redissonClient.getBucket(requestKey);

    return rBucket
        .getAndSet(requestKey, timout, TimeUnit.MINUTES);
  }

  public void deleteFromCache(String key) {
    if (searchItemInCache(key)) {
      RBucket<String> rBucket = redissonClient.getBucket(key);

      if (rBucket != null) {
        rBucket.getAndDelete();
      }
    } else {
      log.debug("UniqueIdentificationNO does not exist in the cache");
    }
  }

  public boolean searchItemInCache(String key) {
    if (key == null) {
      return false;
    }
    try {
      RBucket<String> rBucket = redissonClient.getBucket(key);
      return rBucket.isExists();
    } catch (Exception e) {
      log.error("Error in concurrency MicroService.", e);
    }
    return false;
  }

  public List<String> checkConcurrency(String... keys) {
    List<String> keysFounded = new ArrayList<>();
    for (String key : keys) {
      if (key == null) {
        continue;
      }
      RBucket<String> rBucket = redissonClient.getBucket(key);
      if (rBucket.isExists()) {
        log.info("find concurrency. {} exist in cache", key);
        keysFounded.add(key);
      }
    }
    return keysFounded;
  }
}
