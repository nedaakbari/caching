package com.example.cachemanager;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManagers implements CacheManager {

    private final RedissonClient redissonClient;

//    @Autowired
//    public RedisCacheManagers(RedissonClient redissonClient) {
//        this.redissonClient = redissonClient;
//    }

    @Override
    public void addItemToCache(String key, String value, Long timout) {
        if (key == null)
            return;

        RBucket<String> rBucket = redissonClient.getBucket(key);

        rBucket.set(value, timout, TimeUnit.SECONDS);
    }

    @Override
    public String getItemFromCache(String key) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        return rBucket.isExists() ? rBucket.get() : null;
    }

}