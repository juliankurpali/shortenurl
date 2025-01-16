package com.juli.urlshorten.controller;

import com.juli.urlshorten.model.api.UrlMappingRequest;
import com.juli.urlshorten.model.dto.UrlMappingDTO;
import com.juli.urlshorten.service.urlshorten.UrlShortenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    public UrlShortenController(UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMappingDTO> shortenUrl(@RequestBody UrlMappingRequest urlMappingRequest) {
        return ResponseEntity.ok(urlShortenService.shortenUrl(urlMappingRequest));
    }

    @GetMapping("/g/{shortUrl}")
    public ResponseEntity<UrlMappingDTO> getOriginalUrl(@PathVariable String shortUrl) {
        return ResponseEntity.ok(urlShortenService.findEntityByShortUrl(shortUrl));
    }
}
