package com.example.urlshortener.service;

import com.example.urlshortener.config.AppConfig;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import io.micronaut.http.exceptions.HttpStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    private UrlMappingRepository repository;
    private AppConfig appConfig;
    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        repository = mock(UrlMappingRepository.class);
        appConfig = mock(AppConfig.class);
        when(appConfig.baseUrl()).thenReturn("http://localhost:8080/");
        service = new UrlShortenerService(repository, appConfig);
    }

    @Test
    void testShortenUrlWithCustomAliasSuccess() {
        String fullUrl = "https://example.com";
        String alias = "custom123";

        when(repository.existsById(alias)).thenReturn(false);

        String result = service.shortenUrl(fullUrl, alias);

        assertEquals("http://localhost:8080/custom123", result);
        verify(repository).save(new UrlMapping(alias, fullUrl));
    }

    @Test
    void testShortenUrlWithCustomAliasConflict() {
        String fullUrl = "https://example.com";
        String alias = "custom123";

        when(repository.existsById(alias)).thenReturn(true);

        HttpStatusException ex = assertThrows(HttpStatusException.class, () ->
                service.shortenUrl(fullUrl, alias));

        assertEquals("Alias already taken", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testShortenUrlWithRandomAlias() {
        String fullUrl = "https://example.com";

        // We'll mock existsById to always return false for the generated alias
        when(repository.existsById(anyString())).thenReturn(false);

        String result = service.shortenUrl(fullUrl, null);

        assertTrue(result.startsWith("http://localhost:8080/"));

        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void testGetFullUrlExists() {
        UrlMapping mapping = new UrlMapping("abc123", "https://example.com");
        when(repository.findById("abc123")).thenReturn(Optional.of(mapping));

        Optional<String> result = service.getFullUrl("abc123");

        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get());
    }

    @Test
    void testGetFullUrlNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        Optional<String> result = service.getFullUrl("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteUrlExists() {
        when(repository.existsById("abc123")).thenReturn(true);

        service.deleteUrl("abc123");

        verify(repository).deleteById("abc123");
    }

    @Test
    void testDeleteUrlNotFound() {
        when(repository.existsById("missing")).thenReturn(false);

        HttpStatusException ex = assertThrows(HttpStatusException.class, () ->
                service.deleteUrl("missing"));

        assertEquals("Alias not found", ex.getMessage());
    }

    @Test
    void testListUrls() {
        UrlMapping m1 = new UrlMapping("a1", "https://a.com");
        UrlMapping m2 = new UrlMapping("b2", "https://b.com");

        when(repository.findAll()).thenReturn(List.of(m1, m2));

        List<UrlMapping> result = service.listUrls();

        assertEquals(2, result.size());
        assertEquals("a1", result.get(0).getAlias());
        assertEquals("https://b.com", result.get(1).getFullUrl());
    }
}
