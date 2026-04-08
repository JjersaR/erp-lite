package com.jersa.persistence.mongo.documents;

import java.math.BigDecimal;
import java.util.List;

public record RCatalogItemMetadata(
    String icon,
    String color,
    List<String> nextStatuses,
    BigDecimal fee) {

}
