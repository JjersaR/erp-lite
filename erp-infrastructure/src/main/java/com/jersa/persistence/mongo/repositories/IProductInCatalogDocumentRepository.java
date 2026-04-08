package com.jersa.persistence.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.mongo.documents.ProductInCatalogDocument;

@Repository
public interface IProductInCatalogDocumentRepository extends MongoRepository<ProductInCatalogDocument, String> {

}
