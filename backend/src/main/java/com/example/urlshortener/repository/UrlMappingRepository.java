package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlMapping;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository
public interface UrlMappingRepository extends CrudRepository<UrlMapping, String> {
}
