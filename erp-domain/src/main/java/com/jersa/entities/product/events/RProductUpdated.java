package com.jersa.entities.product.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.product.RProductId;

public record RProductUpdated(
    RProductId productId,
    Instant timestamp) implements IDomainEvent {

}
