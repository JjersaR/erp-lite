package com.jersa.persistence.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.mongo.documents.ProductInCatalogDocument;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductInCatalogDocumentRepository extends MongoRepository<ProductInCatalogDocument, String> {
    Optional<ProductInCatalogDocument> findBySku(String sku);

    List<ProductInCatalogDocument> findByNameContainingIgnoreCase(String name);

    @Query("{ '$text' : {'$search' : ?0 }, 'active' : true }")
    List<ProductInCatalogDocument> findByTextAndActive(String text);

    List<ProductInCatalogDocument> findByCategoryIdAndActiveTrue(String category);

    List<ProductInCatalogDocument> findByActiveTrueOrderByIdAsc();
}