package com.jersa.entities.order.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.order.ROrderId;

public record ROrderCancelled(ROrderId orderId, String reason, Instant timestamp) implements IDomainEvent {

}
