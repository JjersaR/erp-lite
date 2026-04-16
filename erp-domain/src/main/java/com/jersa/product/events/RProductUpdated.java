package com.jersa.product.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.product.RProductId;

public record RProductUpdated(
    RProductId productId,
    Instant timestamp) implements IDomainEvent {

}
