package com.juli.urlshorten.service.urlshorten;

import com.juli.urlshorten.mapper.UrlObjectsMapper;
import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.model.entity.UrlMappingEntity;
import com.juli.urlshorten.repository.UrlShortenRepository;
import com.juli.urlshorten.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlShortenService {

    private final UrlShortenRepository urlShortenRepository;

    @Value("${base.url}")
    private String BASE_URL;

    public UrlShortenService(UrlShortenRepository urlShortenRepository) {
        this.urlShortenRepository = urlShortenRepository;
    }

    @Transactional
    public UrlMappingDTO findEntityByShortUrl(String shortUrl) {
        return urlShortenRepository.findByShortUrl(shortUrl).map(urlMappingEntity -> {
            //If the URL has expired, delete the URL
            if (urlMappingEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
                urlShortenRepository.delete(urlMappingEntity);
                return null;
            }
            //If the URL has not expired, increment the hit count
            urlMappingEntity.setHitCount(urlMappingEntity.getHitCount() + 1);
            //Update the hit count in the database
            saveUrlMappingEntity(urlMappingEntity);
            //Return the URL
            return UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
        }).orElse(null);
    }

    @Transactional
    public UrlMappingDTO shortenUrl(UrlMappingRequest urlMappingRequest) {
        UrlMappingDTO urlMappingDTO = checkUserRequestExistingUrlMapper(urlMappingRequest);
        if (urlMappingDTO != null) {
            return urlMappingDTO;
        }
        //If the URL does not exist, create a new URL
        UrlMappingEntity urlMappingEntity = UrlObjectsMapper.mapToEntityUsingRequest(urlMappingRequest, generateShortUrl());
        saveUrlMappingEntity(urlMappingEntity);
        return UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
    }

    public void saveUrlMappingEntity(UrlMappingEntity urlMappingEntity) {
        urlShortenRepository.save(urlMappingEntity);
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredUrls() {
        urlShortenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    UrlMappingEntity findEntityByOriginalUrl(String originalUrl) {
        return urlShortenRepository.findByOriginalUrl(originalUrl).orElse(null);
    }

    private UrlMappingDTO checkUserRequestExistingUrlMapper(UrlMappingRequest urlMappingRequest) {
        Optional<UserDetails> userDetails = Optional.ofNullable(SecurityUtil.getUserDetails());
        UrlMappingEntity urlMappingEntity = findEntityByOriginalUrl(urlMappingRequest.getOriginalUrl());
        //If the user is the creator of the URL, update the expiry date instead of creating a new URL
        if (userDetails.isPresent() && urlMappingEntity != null && userDetails.get().getUsername().equals(urlMappingEntity.getCreatedBy())) {
            urlMappingEntity.setExpiryDate(LocalDateTime.now().plus(urlMappingRequest.getExpiryOptions().getDuration()));
            saveUrlMappingEntity(urlMappingEntity);
        }
        //If the user is not the creator of the URL, return the existing URL
        if (urlMappingEntity != null) {
            return UrlObjectsMapper.mapToDtoUsingEntity(urlMappingEntity);
        }
        //If the URL does not exist, return null
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
