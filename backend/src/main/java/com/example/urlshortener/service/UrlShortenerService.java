package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Singleton
public class UrlShortenerService {
    private static final String BASE_URL = "http://localhost:8080/";
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public String shortenUrl(String fullUrl, String customAlias) {
        String alias = customAlias != null && !customAlias.isEmpty() ? customAlias : generateRandomAlias();
        if (repository.existsById(alias)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Alias already taken");
        }
        repository.save(new UrlMapping(alias, fullUrl));
        return BASE_URL + alias;
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

    private String generateRandomAlias() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHA_NUM.charAt(random.nextInt(ALPHA_NUM.length())));
        }
        return sb.toString();
    }
}
