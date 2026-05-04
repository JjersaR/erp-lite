package com.jersa.entities.product.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.product.RProductId;
import com.jersa.entities.product.RProductName;
import com.jersa.entities.product.RSKU;
import com.jersa.shared.RMoney;

public record RProductCreated(
    RProductId productId,
    RSKU sku,
    RProductName name,
    RMoney price,
    Instant timestamp) implements IDomainEvent {

}
