package com.juli.urlshorten.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UrlMappingDTO {
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime expiryDate;
    private int hitCount;
}
