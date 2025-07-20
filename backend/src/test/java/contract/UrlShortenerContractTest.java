package contract;

import com.example.urlshortener.controller.UrlShortenerController;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class UrlShortenerContractTest {
    private static GenericContainer mongo =
            new GenericContainer("mongo:4.0");

    @Inject
    @Client("/")
    HttpClient client;

    @BeforeAll
    public static void init() {
        mongo.setPortBindings(List.of("27017:27017"));
        mongo.start();
    }

    @AfterAll
    public static void cleanup() {
        mongo.stop();
    }

    // Happy
    @Test
    void testShortenUrl() {
        HttpRequest<String> request = HttpRequest.POST("/shorten", "{\"fullUrl\": \"https://example.com\", \"customAlias\": \"test-contract\"}")
                .contentType(MediaType.APPLICATION_JSON);
        HttpResponse<UrlShortenerController.ShortUrlResponse> response = client.toBlocking().exchange(request, UrlShortenerController.ShortUrlResponse.class);
        assertEquals(201, response.getStatus().getCode());
        Optional<UrlShortenerController.ShortUrlResponse> body = response.getBody();

        assertTrue(body.isPresent());
        String shortUrl = body.get().shortUrl();
        assertEquals("http://localhost:8080/test-contract", shortUrl);
    }

    @Test
    void testRedirectAfterShortenUrl() {
        HttpRequest<String> request = HttpRequest.POST("/shorten", "{\"fullUrl\": \"https://localhost:8080/missing-always\", \"customAlias\": \"test-contract-redirect\"}")
                .contentType(MediaType.APPLICATION_JSON);
        BlockingHttpClient blocking = client.toBlocking();
        blocking.exchange(request, UrlShortenerController.ShortUrlResponse.class);

        HttpRequest<String> redirectRequest = HttpRequest.GET("/test-contract-redirect");
        HttpResponse<Object> redirectResponse = blocking.exchange(redirectRequest);

        assertEquals(301, redirectResponse.getStatus().getCode());
    }

    // Some Unhappy
    @Test
    void testFailsToShortenWhenIncorrectContentTypeProvided() {
        HttpRequest<String> request = HttpRequest.POST("/shorten", "{\"fullUrl\": \"https://example.com\", \"customAlias\": \"test-contract\"}")
                .contentType(MediaType.TEXT_PLAIN);

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        }, "Should throw HttpClientResponseException");
        assertEquals(415, exception.getStatus().getCode());
        assertEquals("Unsupported Media Type", exception.getMessage());
    }

    @Test
    void testFailsToShortenWithIncorrectMethod() {
        HttpRequest<String> request = HttpRequest.PUT("/shorten", "{\"fullUrl\": \"https://example.com\", \"customAlias\": \"test-contract\"}");
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        }, "Should throw HttpClientResponseException");

        assertEquals(405, exception.getStatus().getCode());
        assertEquals("Method Not Allowed", exception.getMessage());
    }

    @Test
    void testMalformedFullUrl() {
        HttpRequest<String> request = HttpRequest.POST("/shorten", "{\"fullUrl\": \"htts://example.com\", \"customAlias\": \"test-contract\"}")
                .contentType(MediaType.APPLICATION_JSON);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        }, "Should throw HttpClientResponseException");

        assertEquals(400, exception.getStatus().getCode());
        assertEquals("Bad Request", exception.getMessage());
    }

    // Notes to talk about
    // Add more contract validation tests it's important to be as strict as feasible
    // Ordering of when each step fails may need to be consistent when deploying this
    // as a microservice as part of a bigger platform
    // e.g. failed auth is reported after the basic request validation is completed
}
