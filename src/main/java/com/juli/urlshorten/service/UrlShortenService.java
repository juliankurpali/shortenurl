package com.juli.urlshorten.service;

import com.juli.urlshorten.mapper.UrlObjectsMapper;
import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.model.entity.UrlMappingEntity;
import com.juli.urlshorten.repository.UrlShortenRepository;
import com.juli.urlshorten.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UrlShortenService {

    Logger logger = LoggerFactory.getLogger(UrlShortenService.class);

    private final UrlShortenRepository urlShortenRepository;
    private final RedisService redisService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public UrlShortenService(UrlShortenRepository urlShortenRepository, RedisService redisService) {
        this.urlShortenRepository = urlShortenRepository;
        this.redisService = redisService;
    }

    @Transactional
    public UrlMappingDTO findEntityByShortUrl(String shortUrl) {
        try {
            // Attempt to fetch from Redis first
            UrlMappingDTO urlMappingDTO = findUrlMapperByShortUrlFromRedis(shortUrl);
            return urlMappingDTO != null ? urlMappingDTO : findUrlMapperByShortUrlFromDb(shortUrl);
        } catch (Exception ex) {
            // Log and rethrow or handle as needed
            throw new RuntimeException("Error occurred while fetching short URL: " + shortUrl, ex);
        }
    }

    private UrlMappingDTO findUrlMapperByShortUrlFromRedis(String shortUrl) {
        try {
            UrlMappingDTO urlMappingDTORedis = redisService.get(shortUrl);
            if (urlMappingDTORedis != null) {
                urlMappingDTORedis.setHitCount(urlMappingDTORedis.getHitCount() + 1);
                asyncAdjustHitCount(shortUrl);
                return urlMappingDTORedis;
            }
        } catch (Exception ex) {
            // Log the Redis error and fallback to DB
            logger.error("Failed to fetch data from Redis for key: {}", shortUrl, ex);
        }
        return null;
    }

    private UrlMappingDTO findUrlMapperByShortUrlFromDb(String shortUrl) {
        return urlShortenRepository.findByShortUrl(shortUrl).map(urlMappingEntity -> {
            if (urlMappingEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
                urlShortenRepository.delete(urlMappingEntity);
                return null;
            }
            return adjustHitCount(urlMappingEntity);
        }).orElse(null);
    }

    private void asyncAdjustHitCount(String shortUrl) {
        CompletableFuture.runAsync(() -> {
            try {
                urlShortenRepository.findByShortUrl(shortUrl).ifPresent(urlMappingEntity -> {
                    if (urlMappingEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
                        urlShortenRepository.delete(urlMappingEntity);
                    } else {
                        adjustHitCount(urlMappingEntity);
                    }
                });
            } catch (Exception ex) {
                logger.error("Error occurred while adjusting hit count for short URL: {}", shortUrl, ex);
            }
        }, executorService);
    }

    private UrlMappingDTO adjustHitCount(UrlMappingEntity urlMappingEntity) {
        urlMappingEntity.setHitCount(urlMappingEntity.getHitCount() + 1);
        saveUrlMappingEntity(urlMappingEntity);
        UrlMappingDTO urlMappingDTO = UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
        try {
            redisService.save(urlMappingEntity.getShortUrl(), urlMappingDTO, Duration.between(LocalDateTime.now(), urlMappingEntity.getExpiryDate()));
        } catch (Exception ex) {
            logger.error("Failed to save updated data to Redis for key: {}", urlMappingEntity.getShortUrl(), ex);
        }
        return urlMappingDTO;
    }

    @Transactional
    public UrlMappingDTO shortenUrl(UrlMappingRequest urlMappingRequest) {
        try {
            UrlMappingDTO urlMappingDTO = checkUserRequestExistingUrlMapper(urlMappingRequest);
            if (urlMappingDTO != null) {
                return urlMappingDTO;
            }
            UrlMappingEntity urlMappingEntity = UrlObjectsMapper.mapToEntityUsingRequest(urlMappingRequest, generateShortUrl());
            saveUrlMappingEntity(urlMappingEntity);
            return UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while shortening URL: " + urlMappingRequest.getOriginalUrl(), ex);
        }
    }

    public void saveUrlMappingEntity(UrlMappingEntity urlMappingEntity) {
        try {
            urlShortenRepository.save(urlMappingEntity);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving URL mapping entity", ex);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredUrls() {
        try {
            urlShortenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        } catch (Exception ex) {
            logger.error("Error occurred while deleting expired URLs", ex);
        }
    }

    UrlMappingEntity findEntityByOriginalUrl(String originalUrl) {
        return urlShortenRepository.findByOriginalUrl(originalUrl).orElse(null);
    }

    private UrlMappingDTO checkUserRequestExistingUrlMapper(UrlMappingRequest urlMappingRequest) {
        try {
            Optional<UserDetails> userDetails = Optional.ofNullable(SecurityUtil.getUserDetails());
            UrlMappingEntity urlMappingEntity = findEntityByOriginalUrl(urlMappingRequest.getOriginalUrl());
            if (userDetails.isPresent() && urlMappingEntity != null && userDetails.get().getUsername().equals(urlMappingEntity.getCreatedBy())) {
                urlMappingEntity.setExpiryDate(LocalDateTime.now().plus(urlMappingRequest.getExpiryOptions().getDuration()));
                saveUrlMappingEntity(urlMappingEntity);
            }
            if (urlMappingEntity != null) {
                return UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
            }
        } catch (Exception ex) {
            logger.error("Error occurred while checking existing URL mapping", ex);
        }
        return null;
    }

    private String generateShortUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }
}
