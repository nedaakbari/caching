package com.example.caching.caching;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;//todo expire in java 21... why????
public class DistributedCache<K, V> {

    private final RMapCache<K, V> cache;

    private final long relativeTimeout;
    private final long absoluteTimeout;

    public DistributedCache(Config config, String storageName, long relativeTimeout, long absoluteTimeout) {
        var redissonClient = Redisson.create(config);
        cache = redissonClient.getMapCache(storageName);
        this.relativeTimeout = relativeTimeout;
        this.absoluteTimeout = absoluteTimeout;
    }

    public void addToCache(K key, V v) {
        cache.fastPut(key, v, absoluteTimeout, TimeUnit.SECONDS, relativeTimeout, TimeUnit.SECONDS);
    }

    public void deleteToken(K key) {
        cache.fastRemove(key);
    }

    public V get(K key) {
        return cache.get(key);
    }
}
