package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
class UrlShortenerControllerTest {

    private UrlShortenerService service;
    private UrlShortenerController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(UrlShortenerService.class);
        controller = new UrlShortenerController(service);
    }

    @Test
    void testShortenUrl() {
        String fullUrl = "https://example.com";
        String alias = "abc123";
        UrlShortenerController.ShortUrlRequest request = new UrlShortenerController.ShortUrlRequest(fullUrl, null);

        when(service.shortenUrl(fullUrl, null)).thenReturn("http://localhost:8080/" + alias);

        HttpResponse<UrlShortenerController.ShortUrlResponse> response = controller.shorten(request);

        assertEquals(201, response.getStatus().getCode());
        assertEquals("http://localhost:8080/abc123", response.body().shortUrl());

        verify(service).shortenUrl(fullUrl, null);
    }

    @Test
    void testShortenUrlWithAliasProvided() {
        String fullUrl = "https://example.com";
        String alias = "other";
        UrlShortenerController.ShortUrlRequest request = new UrlShortenerController.ShortUrlRequest(fullUrl, alias);

        when(service.shortenUrl(fullUrl, alias)).thenReturn("http://localhost:8080/" + alias);

        HttpResponse<UrlShortenerController.ShortUrlResponse> response = controller.shorten(request);

        assertEquals(201, response.getStatus().getCode());
        assertEquals("http://localhost:8080/other", response.body().shortUrl());

        verify(service).shortenUrl(fullUrl, alias);
    }

    @Test
    void testRedirectFound() {
        String alias = "abc123";
        String fullUrl = "https://example.com";

        when(service.getFullUrl(alias)).thenReturn(Optional.of(fullUrl));

        HttpResponse<?> response = controller.redirect(alias);

        assertEquals(301, response.getStatus().getCode());
        assertEquals(fullUrl, response.getHeaders().get("Location"));

        verify(service).getFullUrl(alias);
    }

    @Test
    void testRedirectNotFound() {
        String alias = "missing";

        when(service.getFullUrl(alias)).thenReturn(Optional.empty());

        HttpResponse<?> response = controller.redirect(alias);

        assertEquals(404, response.getStatus().getCode());

        verify(service).getFullUrl(alias);
    }

    @Test
    void testDelete() {
        String alias = "abc123";

        HttpResponse<?> response = controller.delete(alias);

        assertEquals(204, response.getStatus().getCode());
        verify(service).deleteUrl(alias);
    }

    @Test
    void testListUrls() {
        UrlMapping mapping1 = new UrlMapping("abc123", "https://site1.com");
        UrlMapping mapping2 = new UrlMapping("def456", "https://site2.com");

        when(service.listUrls()).thenReturn(List.of(mapping1, mapping2));

        List<UrlShortenerController.UrlMappingResponse> result = controller.list();

        assertEquals(2, result.size());
        assertEquals("abc123", result.getFirst().alias());
        assertEquals("http://localhost:8080/abc123", result.get(0).shortUrl());

        verify(service).listUrls();
    }
}