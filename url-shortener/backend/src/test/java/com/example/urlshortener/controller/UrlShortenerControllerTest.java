package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
class UrlShortenerControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    UrlShortenerService service;

    @BeforeEach
    void setUp() {
        reset(service);
    }

    @Test
    void testShortenUrl() {
        when(service.shortenUrl("https://example.com", "custom")).thenReturn("http://localhost:8080/custom");
        HttpRequest<?> request = HttpRequest.POST("/shorten", new UrlShortenerController.ShortUrlRequest("https://example.com", "custom"));
        HttpResponse<UrlShortenerController.ShortUrlResponse> response = client.toBlocking().exchange(request, UrlShortenerController.ShortUrlResponse.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals("http://localhost:8080/custom", response.getBody().get().shortUrl());
    }

    @Test
    void testRedirectFound() {
        when(service.getFullUrl("alias")).thenReturn(Optional.of("https://example.com"));
        HttpRequest<?> request = HttpRequest.GET("/alias");
        HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals(URI.create("https://example.com"), response.header("Location"));
    }

    @Test
    void testRedirectNotFound() {
        when(service.getFullUrl("alias")).thenReturn(Optional.empty());
        HttpRequest<?> request = HttpRequest.GET("/alias");
        HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testDeleteUrl() {
        doNothing().when(service).deleteUrl("alias");
        HttpRequest<?> request = HttpRequest.DELETE("/alias");
        HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testListUrls() {
        when(service.listUrls()).thenReturn(Arrays.asList(
            new UrlMapping("alias1", "https://example1.com"),
            new UrlMapping("alias2", "https://example2.com")
        ));
        HttpRequest<?> request = HttpRequest.GET("/urls");
        HttpResponse<List<UrlShortenerController.UrlMappingResponse>> response = client.toBlocking().exchange(request, Argument.of(List.class, UrlShortenerController.UrlMappingResponse.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<UrlShortenerController.UrlMappingResponse> urls = response.getBody().get();
        assertEquals(2, urls.size());
        assertEquals("alias1", urls.get(0).alias());
        assertEquals("https://example1.com", urls.get(0).fullUrl());
        assertEquals("http://localhost:8080/alias1", urls.get(0).shortUrl());
    }
}
