package com.jersa.order.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.order.ROrderId;

public record ROrderConfirmed(
    ROrderId orderId,
    Instant timestamp) implements IDomainEvent {

}
