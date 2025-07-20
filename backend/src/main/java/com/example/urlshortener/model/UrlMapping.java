package com.example.urlshortener.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

@MappedEntity
public class UrlMapping {
    @Id
    private String alias;

    @NotBlank
    private String fullUrl;

    public UrlMapping() {}

    public UrlMapping(String alias, String fullUrl) {
        this.alias = alias;
        this.fullUrl = fullUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
}
