package com.jersa.persistence.mongo.documents;

public record RCatalogItem(
    String id,
    String code,
    String value,
    String description,
    Integer displayOrder,
    RCatalogItemMetadata metadata) {

}
