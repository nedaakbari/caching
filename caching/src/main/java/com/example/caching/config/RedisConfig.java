package com.example.caching.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${redis.client.url}")
    private String url;

    @Value("${redis.client.user}")
    private String user;

    @Value("${redis.client.pass}")
    private String pass;

    @Bean
    public Config config() {
        var config = new Config();
        var configBuilder = config.useSingleServer().setAddress(url);
        if (StringUtils.isNotBlank(user)) {
            configBuilder.setUsername(user);
        }
        if (StringUtils.isNotBlank(pass)) {
            configBuilder.setPassword(pass);
        }
        return config;
    }
}
