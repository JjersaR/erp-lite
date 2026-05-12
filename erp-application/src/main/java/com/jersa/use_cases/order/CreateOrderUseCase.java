package com.jersa.use_cases.order;

import com.jersa.commands.order.RCreateOrderCommand;
import com.jersa.entities.customer.RCustomerInfo;
import com.jersa.entities.order.*;
import com.jersa.entities.product.ProductRoot;
import com.jersa.entities.product.RProductId;
import com.jersa.exceptions.CommandException;
import com.jersa.ports.repositories.IOrderRepositoryPort;
import com.jersa.ports.repositories.IProductRepositoryPort;
import com.jersa.ports.services.ICustomerProviderServicePort;
import com.jersa.ports.services.IOrderConfirmEmailServicePort;
import com.jersa.shared.RCustomerId;
import com.jersa.shared.REmail;
import com.jersa.shared.RMoney;
import com.jersa.shared.RQuantity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * JIRA TICKET: ERP-6734
 * Business Rules:
 * - Customer must exist in external system (JSONPlaceholder)
 * - All products must exist and be active
 * - All products must have sufficient stock
 * - Order number is generated automatically
 * Flow:
 * 1. Validate customer exists
 * 2. Validate products exist
 * 3. Create order items from products (snapshot prices)
 * 4. Create order aggregate (domain generates ID)
 * 5. Persist order (write model - PostgreSQL)
 * 6. Publish domain events (for MongoDB sync, email notifications)
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final IOrderRepositoryPort orderPort;
    private final IProductRepositoryPort productPort;
    private final ICustomerProviderServicePort customerProviderPort;
    private final IOrderConfirmEmailServicePort orderConfirmEmailService;

    public String execute(RCreateOrderCommand command) {
        log.info("Create Order Command: {}", command);
        try {
            RCustomer customer = this.validateAndGet(command.customerId());

            List<OrderItem> orderItems = this.createOrderItems(command.items());

            ROrderNumber orderNumber = this.generateOrderNumber();

            OrderRoot orderRoot = OrderRoot.create(orderNumber, customer, orderItems, command.createdBy());

            OrderRoot saved = this.orderPort.save(orderRoot);

            log.info("Saved Order with id: {}", saved.getId());

            this.sendEmail(orderRoot, getCustomerInfo(command.customerId()));

            return orderRoot.getId().value().toString();
        } catch (IllegalArgumentException iae) {
            log.error("Invalid data: {}", iae.getMessage());
            throw new CommandException("Error on create order message: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while processing CreateOrderCommand", e);
            throw new CommandException(e.getMessage());
        }
    }

    private RCustomer validateAndGet(Long customerId) {
        log.info("Validating customer with id {}", customerId);

        var customerInfo = this.customerProviderPort.findById(customerId).orElseThrow(() -> new CommandException("Customer not found is" + customerId));

        log.info("Customer with id {} found", customerId);

        return RCustomer.of(RCustomerId.of(customerId), customerInfo.name());
    }

    private RCustomerInfo getCustomerInfo(Long customerId) {
        return this.customerProviderPort.findById(customerId).orElseThrow(() -> new CommandException("Customer not found is" + customerId));
    }

    private List<OrderItem> createOrderItems(List<RCreateOrderCommand.OrderItemRequest> commandsItems) {
        log.info("Creating order items");
        return commandsItems.stream().map(this::toOrderItem).toList();
    }

    private OrderItem toOrderItem(RCreateOrderCommand.OrderItemRequest commandsItem) {
        ProductRoot productRoot = this.productPort.findById(RProductId.of(UUID.fromString(commandsItem.productId()))).orElseThrow(() -> new CommandException("product not found"));

        RQuantity quantity = RQuantity.of(commandsItem.quantity());

        return OrderItem.from(productRoot, quantity);
    }

    private ROrderNumber generateOrderNumber() {
        int sequence = (int) (System.currentTimeMillis() % 1000);
        return ROrderNumber.generate(sequence);
    }

    private void publishDomainEvent(OrderRoot orderRoot) {
        var events = orderRoot.getDomainEvents();
        log.info("Publishing domain event for order root {}", orderRoot);

        events.forEach(event -> {
            log.info("Try to publish event {}", event);
            // TODO: send event on queue
        });

        orderRoot.clearDomainEvents();
        log.info("Event published successfully");
    }

    private void sendEmail(OrderRoot order, RCustomerInfo customer) {
        try {
            log.info("Sending mail...");

            this.orderConfirmEmailService.sendEmail(
                    REmail.of(customer.email()),
                    order.getId(),
                    order.getOrderNumber().value(),
                    order.getTotalAmount(),
                    customer.name(),
                    order.getItems().size()
            );
        } catch (Exception e) {
            log.error("Error sending mail", e);
            throw new CommandException("Error sending mail: " + e.getMessage());
        }
    }
}