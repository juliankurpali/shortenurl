package com.juli.urlshorten.configuration.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UrlMappingDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UrlMappingDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Configure the Jackson2JsonRedisSerializer with UrlMappingDTO class
        Jackson2JsonRedisSerializer<UrlMappingDTO> serializer = new Jackson2JsonRedisSerializer<>(new ObjectMapper(),UrlMappingDTO.class);

        // Set key and value serializers
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);

        return redisTemplate;
    }
}
