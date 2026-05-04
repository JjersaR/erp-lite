package com.jersa.entities.order.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.order.ROrderId;

public record ROrderDelivered(
    ROrderId orderId,
    Instant timestamp) implements IDomainEvent {

}
