package com.jersa.product.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.product.RProductId;
import com.jersa.product.RProductName;
import com.jersa.product.RSKU;
import com.jersa.shared.RMoney;

public record RProductCreated(
    RProductId productId,
    RSKU sku,
    RProductName name,
    RMoney price,
    Instant timestamp) implements IDomainEvent {

}
