package com.example.urlshortener.service;

import com.example.urlshortener.config.AppConfig;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Singleton
public class UrlShortenerService {
    private final String baseUrl;
    private static final long START_TIME = LocalDate.of(2025, 7, 20).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository, AppConfig appConfig) {
        this.repository = repository;
        this.baseUrl = appConfig.baseUrl();
    }

    public String shortenUrl(String fullUrl, String customAlias) {
        String alias = customAlias != null && !customAlias.isBlank() ? customAlias : generateNewAlias();
        if (repository.existsById(alias)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Alias already taken");
        }
        repository.save(new UrlMapping(alias, fullUrl));
        return baseUrl + alias;
    }

    public Optional<String> getFullUrl(String alias) {
        return repository.findById(alias).map(UrlMapping::getFullUrl);
    }

    public void deleteUrl(String alias) {
        if (!repository.existsById(alias)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Alias not found");
        }
        repository.deleteById(alias);
    }

    public List<UrlMapping> listUrls() {
        return repository.findAll().stream().toList();
    }

    private String generateNewAlias() {
        long now = System.currentTimeMillis();
        long difference = now - START_TIME;
        return Long.toString(difference, Character.MAX_RADIX);
    }
}
