package com.example.caching;
import com.example.caching.service.BadCredentialsException;
import com.example.caching.service.NoSuchUserFound;
import com.google.common.hash.Hashing;
import com.example.caching.service.UserService;
import com.example.caching.storage.DistributedCache;
import com.example.caching.storage.DistributedCacheFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Component
public class BasicAuthentication {
    private final UserService userService;
    private final DistributedCache<String, Long> cache;

    public BasicAuthentication(UserService userService, DistributedCacheFactory<String, Long> cacheFactory
            , @Value("${basic-storage.timeout-seconds}") Long absoluteTimeout
            , @Value("${basic-storage.name}") String cacheName) {
        this.userService = userService;
        cache = cacheFactory.getCache(cacheName, 0, absoluteTimeout);
    }


    private Long loginUser(String authorization, String scope) throws BadCredentialsException {
        var hashingCredential = Hashing.sha256()
                .hashString(authorization, StandardCharsets.UTF_8)
                .toString();

        var userId = cache.get(hashingCredential);
        if (userId == null) {
            userId = getUserId(authorization, hashingCredential, scope);
        }
        return userId;
    }

    private Long getUserId(String authorization, String hashingBasicAuth, String scope) throws BadCredentialsException {
        try {
            var credential = getUsernameAndPassword(authorization);
            var userFromToken = userService.login(credential[0], credential[1], scope);
            cache.addToCache(hashingBasicAuth, userFromToken.getId());
            return userFromToken.getId();
        } catch (NoSuchUserFound ex) {
            throw new BadCredentialsException(ex);
        }
    }

    private String[] getUsernameAndPassword(String authorization) throws BadCredentialsException {
        var base64Credentials = authorization.substring("Basic ".length());
        var credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        var decodedCredential = extractUsernameAndPassword(credentials);
        if (decodedCredential.length != 2) {
            throw new BadCredentialsException();
        }
        return decodedCredential;
    }

    private String[] extractUsernameAndPassword(String credentials) {
        var colonInd = credentials.indexOf(":");
        if (colonInd == -1)
            return new String[]{credentials, ""};
        return new String[]{credentials.substring(0, colonInd), credentials.substring(colonInd + 1)};
    }

    private boolean isBasicAuthentication(String authorization) {
        return authorization.toLowerCase().startsWith("basic ");
    }
}
