package com.jersa.persistence.mongo.documents;

public record RProductSpecifications(
    String processor,
    String ram,
    String storage,
    String display,
    String weight) {

}
