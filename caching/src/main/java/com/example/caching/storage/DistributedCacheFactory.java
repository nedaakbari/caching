package com.example.caching.storage;
import lombok.RequiredArgsConstructor;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DistributedCacheFactory<K, V> {
    private final Config config;
    private final Map<String, DistributedCache<K, V>> map = new HashMap<>();
    private final Object addLock = new Object();

    public DistributedCache<K, V> getCache(String storageName, long relativeTimeout, long absoluteTimeout) {
        var distributedCache = map.get(storageName);
        return (distributedCache != null ? distributedCache : createCache(storageName, relativeTimeout, absoluteTimeout));
    }

    private DistributedCache<K, V> createCache(String storageName, long relativeTimeout, long absoluteTimeout) {
        synchronized (addLock) {
            return map.computeIfAbsent(storageName, ignored ->
                    new DistributedCache<>(config, storageName, relativeTimeout, absoluteTimeout));
        }
    }
}
