package com.example.cachemanager.concurrentFinal.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@Component
public class ConcurrencyService {

  private final CacheManager cacheManager;
  @Value("${spring.redis.request.timeout}")
  private Long reqTimeOut;
  @Value("${spring.redis.identification.timeout}")
  private Long idTimeOut;
  @Value("${spring.redis.service.timeout}")
  private Long serviceTimeOut;



  public ConcurrencyService(
      RedisCacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public ResponseEntity<?> addItemsToCache(MainContext mainContext, boolean justRequestId) {

    CacheItem item = new CacheItem(mainContext, justRequestId);
    Set<ConcurrencyExceptionType> exceptions = mainContext.getResults()
        .getConcurrencyExceptions();

    try {
      ResponseTypeEnum concurrent = isConcurrent(item, mainContext);
      if (!ResponseTypeEnum.ACCEPTED.equals(concurrent)) {
        return concurrent;
      }

      if (cacheManager.addItemToCache(item.getRequestKey(), reqTimeOut) != null) {
        exceptions.add(ConcurrencyExceptionType.REQUEST_ID_ALREADY_INUSE);
        return (ResponseTypeEnum.CONCURRENCY_EXCEPTION);
      }

      if (!justRequestId) {

        if (cacheManager.addItemToCache(item.getIdentificationKey(), idTimeOut) != null) {
          exceptions.add(ConcurrencyExceptionType.IDENTIFICATION_NUMBER_ALREADY_INUSE);
          return (ResponseTypeEnum.CONCURRENCY_EXCEPTION);
        }

        if (cacheManager.addItemToCache(item.getServiceKey(), serviceTimeOut) != null) {
          exceptions.add(ConcurrencyExceptionType.SERVICE_NUMBER_ALREADY_INUSE);
          return (ResponseTypeEnum.CONCURRENCY_EXCEPTION);
        }

      }

    } catch (Exception e) {
      exceptions
          .add(ConcurrencyExceptionType.SERVICE_NUMBER_ALREADY_INUSE);
      log.error("addItemsToCache blocked by exception!", e);
      return (ResponseTypeEnum.CONCURRENCY_EXCEPTION);
    }

    return (ResponseTypeEnum.ACCEPTED);
  }


  // note: requestId has been deleted with TTL
  public ResponseTypeEnum deleteItemsFromCache(MainContext mainContext) {
    CacheItem item = new CacheItem(mainContext, false);
    try {
      cacheManager.deleteFromCache(item.getIdentificationKey());
      cacheManager.deleteFromCache(item.getServiceKey());
    } catch (Exception e) {
      log.info("Exception in delete from Cache", e);
      mainContext.getResults().getConcurrencyExceptions()
          .add(ConcurrencyExceptionType.OTHER_EXCEPTION);
      return ResponseTypeEnum.SHAHKAR_EXCEPTION;
    }
    return ResponseTypeEnum.ACCEPTED;
  }

  public boolean searchItemInCache(String key) {
    return cacheManager.searchItemInCache(key);
  }

  private ResponseTypeEnum isConcurrent(CacheItem item, MainContext mainContext) {
    List<String> keysFounded = cacheManager
        .checkConcurrency(item.getIdentificationKey(), item.getRequestKey(),
            item.getServiceKey());
    ResponseTypeEnum statusCode = ResponseTypeEnum.ACCEPTED;
    if (keysFounded.size() > 0) {
      statusCode = ResponseTypeEnum.CONCURRENCY_EXCEPTION;
      for (String reply : keysFounded) {
        if (reply.startsWith(CacheItem.REQUEST_KEY_PREFIX)) {
          statusCode = ResponseTypeEnum.DUPLICATE_REQUEST_ID;
          mainContext.getResults().getConcurrencyExceptions()
              .add(ConcurrencyExceptionType.REQUEST_ID_ALREADY_INUSE);
        }
        if (reply.startsWith(CacheItem.IDENTIFICATION_KEY_PREFIX)) {
          mainContext.getResults().getConcurrencyExceptions()
              .add(ConcurrencyExceptionType.IDENTIFICATION_NUMBER_ALREADY_INUSE);
        }
        if (reply.startsWith(CacheItem.SERVICE_KEY_PREFIX)) {
          mainContext.getResults().getConcurrencyExceptions()
              .add(ConcurrencyExceptionType.SERVICE_NUMBER_ALREADY_INUSE);
        }
      }

      log.debug("concurrency exist in {} item(s)!",
          mainContext.getResults().getConcurrencyExceptions().size());
    }
    return statusCode;
  }

}
