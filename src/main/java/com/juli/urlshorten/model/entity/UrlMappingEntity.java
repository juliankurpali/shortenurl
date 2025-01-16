package com.juli.urlshorten.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mapping")
@Setter
@Getter
public class UrlMappingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String originalUrl;
    @Column(nullable = false, unique = true)
    private String shortUrl;
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    @Column(nullable = false)
    private int hitCount;
    @Column(nullable = false)
    private LocalDateTime createdDate;
    @Column(nullable = false)
    private String createdBy;
}
