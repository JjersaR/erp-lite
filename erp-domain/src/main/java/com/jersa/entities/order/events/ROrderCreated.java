package com.jersa.entities.order.events;

import java.time.Instant;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.order.ROrderId;
import com.jersa.shared.RCustomerId;
import com.jersa.shared.RMoney;

public record ROrderCreated(
    ROrderId orderId,
    RCustomerId customerId,
    String customerName,
    RMoney totalAmount,
    Instant timestamp) implements IDomainEvent {

}
