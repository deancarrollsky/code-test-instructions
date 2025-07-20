package com.example.urlshortener.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UrlMapping that = (UrlMapping) o;
        return Objects.equals(alias, that.alias) && fullUrl.equals(that.fullUrl);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(alias);
        result = 31 * result + fullUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UrlMapping.class.getSimpleName() + "[", "]").add("alias='" + alias + "'").add("fullUrl='" + fullUrl + "'").toString();
    }
}
