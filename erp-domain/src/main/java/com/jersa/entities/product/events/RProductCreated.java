package com.jersa.entities.product.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.product.*;
import com.jersa.shared.RMoney;

// POST -> SQL (write) -> Rabbit(ProductCreatedEvent) -> Listener -> Update Mongo
public record RProductCreated(
        RProductId productId,
        RSKU sku,
        RProductName name,
        RMoney price,
        Instant timestamp,
        String description,
        RStock stock,
        RCategoryReference category,
        RProductImage image,
        boolean active
) implements IDomainEvent {

}
