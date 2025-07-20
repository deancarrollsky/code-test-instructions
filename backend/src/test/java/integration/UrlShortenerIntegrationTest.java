package integration;

import com.example.urlshortener.Application;
import com.example.urlshortener.service.UrlShortenerService;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.Micronaut;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
class UrlShortenerIntegrationTest {

    @Inject
    UrlShortenerService service;

    private static GenericContainer mongo =
            new GenericContainer("mongo:4.0");

    @BeforeAll
    public static void init() {
        mongo.setPortBindings(List.of("27017:27017"));
        mongo.start();
    }

    @AfterAll
    public static void cleanup() {
        mongo.stop();
    }

    @Test
    void testShortenUrlAndGetFullUrl() {
        String shortUrl = service.shortenUrl("https://example.com", "test-integration");
        assertEquals("http://localhost:8080/test-integration", shortUrl);

        Optional<String> fullUrl = service.getFullUrl("test-integration");
        assertTrue(fullUrl.isPresent());
        assertEquals("https://example.com", fullUrl.get());
    }

    @Test
    void testDeleteUrl() {
        service.shortenUrl("https://example.com", "test-delete");
        service.deleteUrl("test-delete");

        Optional<String> fullUrl = service.getFullUrl("test-delete");
        assertFalse(fullUrl.isPresent());
    }

    // Would be adding failure conditions here too
    // Priming mongo db down / exceptions for example and expected response
}
