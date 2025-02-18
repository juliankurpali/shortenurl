package com.juli.urlshorten.repository;

import com.juli.urlshorten.model.entity.UrlMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlShortenRepository extends JpaRepository<UrlMappingEntity, Long> {
    Optional<UrlMappingEntity> findByShortUrl(String shortUrl);

    Optional<UrlMappingEntity> findByOriginalUrl(String originalUrl);

    @Transactional
    void deleteByExpiryDateBefore(LocalDateTime expiryDate);
}
