package com.juli.urlshorten.service;

import com.juli.urlshorten.model.dto.UrlMappingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Value("${redis.max-timeout}")
    private long maxTimeout;

    private final RedisTemplate<String, UrlMappingDTO> redisTemplate;

    public RedisService(RedisTemplate<String, UrlMappingDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Save data to Redis
    public void save(String key, UrlMappingDTO value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, getMaxTimeout(timeout), TimeUnit.SECONDS);
    }

    // Retrieve data from Redis
    public UrlMappingDTO get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Check if a key exists in Redis
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Set a timeout for a key in Redis
    public long getMaxTimeout(Duration timeout){
        return Math.min(timeout.getSeconds(), maxTimeout);
    }
}

