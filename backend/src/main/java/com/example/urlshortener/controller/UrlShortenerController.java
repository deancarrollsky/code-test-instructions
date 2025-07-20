package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Controller
public class UrlShortenerController {
    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @Post("/shorten")
    public HttpResponse<ShortUrlResponse> shorten(@Body ShortUrlRequest request) {
        String shortUrl = service.shortenUrl(request.fullUrl, request.customAlias);
        return HttpResponse.created(new ShortUrlResponse(shortUrl));
    }

    @Get("/{alias}")
    public HttpResponse<?> redirect(@PathVariable String alias) {
        return service.getFullUrl(alias)
                .map(url -> HttpResponse.redirect(UriBuilder.of(url).build()))
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{alias}")
    public HttpResponse<?> delete(@PathVariable String alias) {
        service.deleteUrl(alias);
        return HttpResponse.noContent();
    }

    @Get("/urls")
    public List<UrlMappingResponse> list() {
        return service.listUrls().stream()
                .map(url -> new UrlMappingResponse(url.getAlias(), url.getFullUrl(), "http://localhost:8080/" + url.getAlias()))
                .toList();
    }

    public record ShortUrlRequest(@NotBlank String fullUrl, String customAlias) {}
    public record ShortUrlResponse(String shortUrl) {}
    public record UrlMappingResponse(String alias, String fullUrl, String shortUrl) {}
}
