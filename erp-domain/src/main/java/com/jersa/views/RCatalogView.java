package com.jersa.views;

import com.jersa.enums.ECatalogType;

import java.time.Instant;
import java.util.List;

public record RCatalogView(
        Boolean active,
        String name,
        String description,
        ECatalogType type,
        Instant createdAt,
        Instant updatedAt,
        List<RItemsView> items
) {
}
