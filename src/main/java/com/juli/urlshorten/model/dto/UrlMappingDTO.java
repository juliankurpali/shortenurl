package com.juli.urlshorten.model.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
public class UrlMappingDTO {
    @Id
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime expiryDate;
    private int hitCount;
}
