package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
class UrlShortenerServiceTest {

    @Inject
    UrlShortenerService service;

    @Inject
    UrlMappingRepository repository;

    @BeforeEach
    void setUp() {
        reset(repository);
    }

    @Test
    void testShortenUrlWithCustomAlias() {
        when(repository.existsById("custom")).thenReturn(false);
        when(repository.save(any(UrlMapping.class))).thenReturn(new UrlMapping("custom", "https://example.com"));
        String shortUrl = service.shortenUrl("https://example.com", "custom");
        assertEquals("http://localhost:8080/custom", shortUrl);
        verify(repository).save(argThat(url -> url.getAlias().equals("custom") && url.getFullUrl().equals("https://example.com")));
    }

    @Test
    void testShortenUrlWithRandomAlias() {
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.save(any(UrlMapping.class))).thenReturn(new UrlMapping("random123", "https://example.com"));
        String shortUrl = service.shortenUrl("https://example.com", null);
        assertTrue(shortUrl.startsWith("http://localhost:8080/"));
        assertEquals(24, shortUrl.length()); // BASE_URL length (21) + 8-char alias
        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void testShortenUrlWithDuplicateAlias() {
        when(repository.existsById("custom")).thenReturn(true);
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> service.shortenUrl("https://example.com", "custom"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Alias already taken", exception.getMessage());
    }

    @Test
    void testGetFullUrlFound() {
        when(repository.findById("alias")).thenReturn(Optional.of(new UrlMapping("alias", "https://example.com")));
        Optional<String> fullUrl = service.getFullUrl("alias");
        assertTrue(fullUrl.isPresent());
        assertEquals("https://example.com", fullUrl.get());
    }

    @Test
    void testGetFullUrlNotFound() {
        when(repository.findById("alias")).thenReturn(Optional.empty());
        Optional<String> fullUrl = service.getFullUrl("alias");
        assertFalse(fullUrl.isPresent());
    }

    @Test
    void testDeleteUrlSuccess() {
        when(repository.existsById("alias")).thenReturn(true);
        service.deleteUrl("alias");
        verify(repository).deleteById("alias");
    }

    @Test
    void testDeleteUrlNotFound() {
        when(repository.existsById("alias")).thenReturn(false);
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> service.deleteUrl("alias"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Alias not found", exception.getMessage());
    }

    @Test
    void testListUrls() {
        when(repository.findAll()).thenReturn(Arrays.asList(
            new UrlMapping("alias1", "https://example1.com"),
            new UrlMapping("alias2", "https://example2.com")
        ));
        List<UrlMapping> urls = service.listUrls();
        assertEquals(2, urls.size());
        assertEquals("alias1", urls.get(0).getAlias());
        assertEquals("https://example1.com", urls.get(0).getFullUrl());
        assertEquals("alias2", urls.get(1).getAlias());
        assertEquals("https://example2.com", urls.get(1).getFullUrl());
    }
}
