package com.jersa.commands.helpers;

import com.jersa.entities.order.OrderRoot;
import com.jersa.entities.order.ROrderId;
import com.jersa.exceptions.CommandException;
import com.jersa.ports.repositories.IOrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHelper {
    private final IOrderRepositoryPort orderPort;

    public OrderRoot findOrderById(String orderId) {
        log.info("Finding order by id {}", orderId);

        return this.orderPort.findById(ROrderId.of(UUID.fromString(orderId))).orElseThrow(() -> new CommandException("Order not found"));
    }
}
