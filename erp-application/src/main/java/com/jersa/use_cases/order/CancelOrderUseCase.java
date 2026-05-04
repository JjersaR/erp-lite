package com.jersa.use_cases.order;

import com.jersa.commands.helpers.CommandHelper;
import com.jersa.commands.order.RCancelOrderCommand;
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
public class CancelOrderUseCase {
    private final IOrderRepositoryPort orderPort;
    private final CommandHelper commandHelper;

    public void execute(RCancelOrderCommand command) {
        try {
            log.info("Cancel Order {}", command.orderId());

            var orderRoot = this.commandHelper.findOrderById(command.orderId());

            orderRoot.cancel(command.reason());
            this.orderPort.save(orderRoot);

            log.info("Canceled order {}", command.orderId());
        } catch (Exception e) {
            log.error("Error on cancel order {}", command.orderId(), e);
            throw new CommandException("Error on cancel order");
        }
    }
}