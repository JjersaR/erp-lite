package com.jersa.order.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.order.ROrderId;

public record ROrderCancelled(ROrderId orderId, String reason, Instant timestamp) implements IDomainEvent {

}
