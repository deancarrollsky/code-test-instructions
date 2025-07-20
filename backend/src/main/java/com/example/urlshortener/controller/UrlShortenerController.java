package com.example.urlshortener.controller;

import com.example.urlshortener.config.AppConfig;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.net.URL;
import java.util.List;

@Controller
public class UrlShortenerController {
    private final UrlShortenerService service;
    private final AppConfig appConfig;

    public UrlShortenerController(UrlShortenerService service, AppConfig appConfig) {
        this.service = service;
        this.appConfig = appConfig;
    }

    @Post(value = "/shorten")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<ShortUrlResponse> shorten(@Body ShortUrlRequest request) {
        URL fullUrl = request.fullUrl;
        String shortUrl = service.shortenUrl(fullUrl.toString(), request.customAlias);
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
    @Produces(MediaType.APPLICATION_JSON)
    public List<UrlMappingResponse> list() {
        return service.listUrls().stream()
                .map(url -> new UrlMappingResponse(url.getAlias(), url.getFullUrl(),  appConfig.baseUrl() + url.getAlias()))
                .toList();
    }

    @Serdeable
    public record ShortUrlRequest(@NotNull URL fullUrl, String customAlias) {}
    @Serdeable
    public record ShortUrlResponse(String shortUrl) {}
    @Serdeable
    public record UrlMappingResponse(String alias, String fullUrl, String shortUrl) {}
}
