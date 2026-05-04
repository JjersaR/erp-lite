package com.jersa.use_cases.order;

import com.jersa.commands.helpers.CommandHelper;
import com.jersa.commands.order.RUpdateOrderStatusCommand;
import com.jersa.entities.order.OrderRoot;
import com.jersa.exceptions.CommandException;
import com.jersa.ports.repositories.IOrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateOrderStatusUseCase {
    private final IOrderRepositoryPort orderPort;
    private final CommandHelper commandHelper;

    public String execute(RUpdateOrderStatusCommand command) {
        try {
            OrderRoot orderRoot = this.commandHelper.findOrderById(command.orderId());

            log.info("Current order status: {}", orderRoot.getStatus());

            this.updateStatus(orderRoot, command.newStatus());

            var saved = this.orderPort.save(orderRoot);

            log.info("Saved order status: {}", saved.getStatus());

            return saved.getStatus().toString();
        }catch (IllegalStateException ise) {
            log.error("Error updating order status", ise);
            throw  new CommandException("Error updating order status");
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw  new CommandException("Unexpected error updating order status");
        }
    }

    private void updateStatus(OrderRoot order, String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED" -> order.confirm();
            case "SHIPPED" -> order.ship();
            case "DELIVERED" -> order.deliver();
            default -> throw new CommandException("Unknown status " + status);
        }
    }
}
