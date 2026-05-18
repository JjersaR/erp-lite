package com.jersa.persistence.rabbit.adapters;

import com.jersa.common.IDomainEvent;
import com.jersa.entities.product.events.RProductCreated;
import com.jersa.exceptions.MyBussinessException;
import com.jersa.persistence.rabbit.config.RabbitMqConfig;
import com.jersa.persistence.rabbit.dtos.ProductCreatedMessage;
import com.jersa.ports.messages.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQEventPublisherAdapter implements EventPublisherPort {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(IDomainEvent event) {
        if (event instanceof RProductCreated) {
            final var message = this.toMessage((RProductCreated) event);

            this.rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, message);

            log.info("Published product created event: {}, in exchange: {}", event, RabbitMqConfig.EXCHANGE);
        } else {
            log.warn("Unable to publish product created event: {}", event);
            throw new MyBussinessException("Event is not supported");
        }
    }

    private ProductCreatedMessage toMessage(RProductCreated event) {
        return new ProductCreatedMessage(
                event.productId().value().toString(),
                event.sku().value(),
                event.name().value(),
                event.price().amount(),
                event.price().currency().getCurrencyCode(),
                event.timestamp(),
                event.description(),
                event.stock().value(),
                event.category().categoryId(),
                event.image() != null ? event.image().imageUrl() : null,
                event.active()
        );
    }
}