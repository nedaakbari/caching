package com.example.cachemanager.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributeLock {
    private final RedissonClient redisson;

//  public DistributeLock(RedissonClient redisson) {
//    this.redisson = redisson;
//  }

    public DistributeLockHolder getLockHolder(String lockName) {
        return getLockHolder(true, lockName);
    }

    public DistributeLockHolder getLockHolder(boolean takeLock, String lockName) {
        return new DistributeLockHolder(redisson, takeLock, lockName);
    }
}
