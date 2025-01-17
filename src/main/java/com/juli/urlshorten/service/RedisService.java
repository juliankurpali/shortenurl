package com.juli.urlshorten.service;

import com.juli.urlshorten.model.dto.UrlMappingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class RedisService {

    @Value("${redis.max-timeout}")
    private long maxTimeout;

    private final RedisTemplate<String, UrlMappingDTO> redisTemplate;

    public RedisService(RedisTemplate<String, UrlMappingDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, UrlMappingDTO value, Duration timeout) {
        executeWithRedisExceptionHandling(() -> {
            redisTemplate.opsForValue().set(key, value, getMaxTimeout(timeout), TimeUnit.SECONDS);
            return null;
        }, "Failed to save key: " + key);
    }

    public UrlMappingDTO get(String key) {
        return executeWithRedisExceptionHandling(
                () -> redisTemplate.opsForValue().get(key),
                "Failed to retrieve key: " + key
        );
    }

    public boolean exists(String key) {
        return executeWithRedisExceptionHandling(
                () -> Boolean.TRUE.equals(redisTemplate.hasKey(key)),
                "Failed to check existence for key: " + key
        );
    }

    public void delete(String key) {
        executeWithRedisExceptionHandling(
                () -> redisTemplate.delete(key),
                "Failed to delete key: " + key
        );
    }

    private <T> T executeWithRedisExceptionHandling(Supplier<T> redisOperation, String errorMessage) {
        try {
            return redisOperation.get();
        } catch (RedisConnectionFailureException ex) {
            throw new RedisConnectionFailureException(errorMessage, ex);
        } catch (RedisSystemException ex) {
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error: " + errorMessage, ex);
        }
    }

    private long getMaxTimeout(Duration timeout) {
        return Math.min(timeout.getSeconds(), maxTimeout);
    }
}
