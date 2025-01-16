package com.juli.urlshorten.mapper;

import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.model.entity.UrlMappingEntity;
import com.juli.urlshorten.util.SecurityUtil;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Optional;

public class UrlObjectsMapper {
    public static UrlMappingEntity mapToEntityUsingRequest(UrlMappingRequest urlMappingRequest, String shortUrl) {
        Optional<UserDetails> userDetails = Optional.ofNullable(SecurityUtil.getUserDetails());
        UrlMappingEntity urlMappingEntity = new UrlMappingEntity();
        urlMappingEntity.setShortUrl(shortUrl);
        urlMappingEntity.setOriginalUrl(urlMappingRequest.getOriginalUrl());
        urlMappingEntity.setExpiryDate(LocalDateTime.now().plus(urlMappingRequest.getExpiryOptions().getDuration()));
        urlMappingEntity.setHitCount(0);
        urlMappingEntity.setCreatedDate(LocalDateTime.now());
        urlMappingEntity.setCreatedBy(userDetails.map(UserDetails::getUsername).orElse("Anonymous"));
        return urlMappingEntity;
    }

    public static UrlMappingDTO mapToDtoUsingEntity(UrlMappingEntity urlMappingEntity) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setOriginalUrl(urlMappingEntity.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMappingEntity.getShortUrl());
        urlMappingDTO.setExpiryDate(urlMappingEntity.getExpiryDate());
        return urlMappingDTO;
    }
}
