package com.jersa.persistence.mongo.repositories;

import com.jersa.enums.ECatalogType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.mongo.documents.CatalogDocument;

import java.util.Optional;

@Repository
public interface ICatalogRepository extends MongoRepository<CatalogDocument, String> {

    Optional<CatalogDocument> findByCatalogType(ECatalogType catalogType);

}
