package com.juli.urlshorten.controller;

import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.service.UrlShortenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @Value("${base.url}")
    private String BASE_URL;

    public UrlShortenController(UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }

    @Operation(summary = "Shorten a URL", description = "This endpoint takes a long URL and generates a shortened version of the URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL shortened successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL request")
    })
    @PostMapping(value = "/shorten", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlMappingDTO> shortenUrl(@Parameter(description = "Request containing the original long URL and expiryOptions") @RequestBody UrlMappingRequest urlMappingRequest, @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        UrlMappingDTO urlMappingDTO = urlShortenService.shortenUrl(urlMappingRequest);
        urlMappingDTO.setShortUrl(BASE_URL+ urlMappingDTO.getShortUrl());

        return ResponseEntity.ok(urlMappingDTO);
    }

    @Operation(summary = "Get the original URL from a shortened URL", description = "This endpoint takes a shortened URL and retrieves the original long URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Original URL retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found")
    })
    @GetMapping(value = "/r/{shortUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlMappingDTO> getOriginalUrl(@Parameter(description = "The shortened URL") @PathVariable String shortUrl, @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        UrlMappingDTO urlMappingDTO = urlShortenService.findEntityByShortUrl(shortUrl);
        urlMappingDTO.setShortUrl(BASE_URL+ urlMappingDTO.getShortUrl());

        return ResponseEntity.ok(urlMappingDTO);
    }
}
