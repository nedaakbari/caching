package com.example.cachemanager.concurrentFinal.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${redis.client.url}")
    private String url;
    @Value("${redis.client.pass}")
    private String pass;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if(StringUtils.isEmpty(pass))
            config.useSingleServer()
            .setAddress(url);
        else
            config.useSingleServer()
            .setAddress(url).setPassword(pass);
        return Redisson.create(config);
    }
}
