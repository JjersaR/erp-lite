package com.jersa.persistence.jpa.adapters;

import com.jersa.entities.order.OrderItem;
import com.jersa.entities.order.OrderRoot;
import com.jersa.entities.order.ROrderId;
import com.jersa.entities.order.ROrderNumber;
import com.jersa.persistence.jpa.entities.OrderEntity;
import com.jersa.persistence.jpa.entities.OrderProductEntity;
import com.jersa.persistence.jpa.mappers.IOrderJpaMapper;
import com.jersa.persistence.jpa.repositories.IOrderEntityRepository;
import com.jersa.ports.repositories.IOrderRepositoryPort;
import com.jersa.shared.RCustomerId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Repository
@AllArgsConstructor
public class OrdersRepositoryAdapter implements IOrderRepositoryPort {
    private final IOrderEntityRepository repository;
    private IOrderJpaMapper mapper;

    @Override
    public OrderRoot save(OrderRoot orderRoot) {
        log.info("Saving Order Root {}", orderRoot);
        try {
            OrderEntity entity = this.mapper.toEntity(orderRoot);

            entity.setId(orderRoot.getId().value());

            this.zipOrderItems(orderRoot, entity);

            var savedEntity = this.repository.save(entity);

            log.info("Saved Order Root {}", savedEntity);

            return this.mapper.toDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error on persistence order root, ", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<OrderRoot> findById(ROrderId orderId) {
        log.info("Finding all Order Root by id {}", orderId.value());
        try {
            Optional<OrderEntity> entityOpt = this.repository.findById(orderId.value());

            if (entityOpt.isEmpty()) {
                log.info("No Order Root found with id {}", orderId.value());
                return Optional.empty();
            }

            return Optional.of(this.mapper.toDomain(entityOpt.get()));
        } catch (Exception e) {
            log.error("Error on finding by id, ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<OrderRoot> findByOrderNumber(ROrderNumber orderNumber) {
        log.info("Finding Order Root by Order number {}", orderNumber.value());
        try {
            var entityOpt = this.repository.findByOrderNumber(orderNumber.value());

            if (entityOpt.isEmpty()) {
                log.info("No Order Root found with this order number, {}", orderNumber.value());
                return Optional.empty();
            }

            return entityOpt.map(entity -> this.mapper.toDomain(entity));
        } catch (Exception e) {
            log.error("Error on finding by order number, ", e);
            return Optional.empty();
        }
    }

    @Override
    public List<OrderRoot> findByCustomerId(RCustomerId customerId) {
        log.info("Finding all Order Root by customer id {}", customerId.value());
        try {
            var entities = this.repository.findByCustomerId(customerId.value());

            if (entities.isEmpty()) {
                log.info("No Order Root found with this customer id, {}", customerId.value());
                return Collections.emptyList();
            }

            return entities.stream().map(entity -> {
                try {
                    return this.mapper.toDomain(entity);
                } catch (Exception e) {
                    log.error("Failed to map order entity to domain: {}", entity.getId(), e);
                    return null;
                }
            }).filter(Objects::nonNull).toList();
        } catch (Exception e) {
            log.error("Error on finding by order number, ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(OrderRoot orderRoot) {
        log.info("Deleting Order Root {}", orderRoot);
        try {
            this.repository.deleteById(orderRoot.getId().value());
            log.info("Deleted Order Root successfully.");
        } catch (Exception e) {
            log.error("Error on deleting this Order Root, ", e);
            throw new IllegalStateException(e);
        }
    }

    private void zipOrderItems(OrderRoot orderRoot, OrderEntity orderEntity) {
        if (orderRoot.getItems().isEmpty() || orderRoot.getItems() == null) {
            log.debug("Items EMPTY");
            return;
        }
        List<OrderProductEntity> itemsEntities = orderEntity.getItems();
        List<OrderItem> itemsDomain = orderRoot.getItems();

        IntStream.range(0, itemsDomain.size()).forEach(i -> {
            itemsEntities.get(i).setId(itemsDomain.get(i).getId().value());
        });
    }
}
