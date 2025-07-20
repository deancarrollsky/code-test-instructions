package com.example.urlshortener.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppConfig(String baseUrl) {
}
