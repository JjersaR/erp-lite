package com.jersa.persistence.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.mongo.documents.CatalogDocument;

@Repository
public interface ICatalogRepository extends MongoRepository<CatalogDocument, String> {

}
