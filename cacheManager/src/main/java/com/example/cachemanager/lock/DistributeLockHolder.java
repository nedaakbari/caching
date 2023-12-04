package com.example.cachemanager.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class DistributeLockHolder implements AutoCloseable {

  private final RLock lock;

  DistributeLockHolder(RedissonClient redisson, boolean needLock, String lockName) {
    if (needLock) {
      this.lock = redisson.getLock(lockName);
    } else {
      this.lock = null;
    }
  }

  @Override
  public void close() throws RuntimeException {
    if (this.lock != null) {
      lock.forceUnlock();
    }
  }
}
