package com.jersa.persistence.rabbit.dtos;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductCreatedMessage(
        String productId,
        String sku,
        String name,
        BigDecimal price,
        String currency,
        Instant timestamp,
        String description,
        Integer stock,
        String category,
        String imageUrl,
        boolean active
) {
}
