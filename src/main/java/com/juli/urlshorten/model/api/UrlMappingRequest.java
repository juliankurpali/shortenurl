package com.juli.urlshorten.model.api;

import com.juli.urlshorten.model.enums.ExpiryOptions;
import lombok.Data;

@Data
public class UrlMappingRequest {
    private String requestedUrl;
    private ExpiryOptions expiryOptions = ExpiryOptions.FIVE_MINUTES;
}
