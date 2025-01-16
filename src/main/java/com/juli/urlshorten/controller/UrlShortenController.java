package com.juli.urlshorten.controller;

import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.service.UrlShortenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @Value("${base.url}")
    private String BASE_URL;

    public UrlShortenController(UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMappingDTO> shortenUrl(@RequestBody UrlMappingRequest urlMappingRequest) {

        UrlMappingDTO urlMappingDTO = urlShortenService.shortenUrl(urlMappingRequest);
        urlMappingDTO.setShortUrl(BASE_URL+ urlMappingDTO.getShortUrl());

        return ResponseEntity.ok(urlMappingDTO);
    }

    @GetMapping("/r/{shortUrl}")
    public ResponseEntity<UrlMappingDTO> getOriginalUrl(@PathVariable String shortUrl) {

        UrlMappingDTO urlMappingDTO = urlShortenService.findEntityByShortUrl(shortUrl);
        urlMappingDTO.setShortUrl(BASE_URL+ urlMappingDTO.getShortUrl());

        return ResponseEntity.ok(urlMappingDTO);
    }
}
