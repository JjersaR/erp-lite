package com.jersa.persistence.jpa.mappers;

import com.jersa.entities.order.*;
import com.jersa.entities.product.RProductId;
import com.jersa.persistence.jpa.entities.OrderEntity;
import com.jersa.persistence.jpa.entities.OrderProductEntity;
import com.jersa.persistence.jpa.entities.ProductEntity;
import com.jersa.shared.RAuditInfo;
import com.jersa.shared.RCustomerId;
import com.jersa.shared.RMoney;
import com.jersa.shared.RQuantity;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;

/**
 * Anti-Corruption Layer between OrderRoot (Domain) and OrderEntity (JPA).
 * Also maps OrderItem ←→ OrderProductEntity.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IOrderJpaMapper {
    // ── Domain → Entity (Order) ─────────────────────────────────────────
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "orderNumber.value", target = "orderNumber")
    @Mapping(source = "customer.customerId.value", target = "customerId")
    @Mapping(source = "customer.customerName", target = "customerName")
    @Mapping(source = "status.value", target = "status")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "totalAmount.amount", target = "totalAmount")
    @Mapping(source = "totalAmount.currency", target = "currency", qualifiedByName = "currencyToString")
    @Mapping(source = "auditInfo.createdBy", target = "createdBy")
    @Mapping(source = "auditInfo.createdAt", target = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "auditInfo.updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "orderDate", ignore = true)
    OrderEntity toEntity(OrderRoot domain);

    // ── Domain → Entity (OrderItem) ─────────────────────────────────────
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "productReference", target = "product", qualifiedByName = "productIdToEntity")
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "quantity.value", target = "quantity")
    @Mapping(source = "unitPrice.amount", target = "unitPrice")
    @Mapping(source = "subtotal.amount", target = "subtotal")
    @Mapping(target = "order", ignore = true)
    OrderProductEntity toItemEntity(OrderItem domain);

    /**
     * Sets the bidirectional order ←→ items relationship after mapping.
     */
    @AfterMapping
    default void setOrderBackReference(@MappingTarget OrderEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
    }

    // ── Entity → Domain (Order) ─────────────────────────────────────────

    /**
     * Reconstitutes an OrderRoot from a JPA entity.
     * Uses reflection to access the private constructor since domain aggregates
     * enforce encapsulated construction via factory methods.
     */
    default OrderRoot toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            List<OrderItem> items = entity.getItems() != null
                    ? entity.getItems().stream().map(this::toItemDomain).toList()
                    : List.of();

            var constructor = OrderRoot.class.getDeclaredConstructor(
                    ROrderId.class, ROrderNumber.class, RCustomer.class, ROrderStatus.class,
                    List.class, RMoney.class, RAuditInfo.class
            );
            constructor.setAccessible(true);

            return constructor.newInstance(
                    ROrderId.of(entity.getId()),
                    ROrderNumber.of(entity.getOrderNumber()),
                    RCustomer.of(
                            RCustomerId.of(entity.getCustomerId()),
                            entity.getCustomerName()
                    ),
                    ROrderStatus.of(entity.getStatus()),
                    items,
                    RMoney.of(
                            entity.getTotalAmount(),
                            Currency.getInstance(entity.getCurrency())
                    ),
                    new RAuditInfo(
                            entity.getCreatedBy(),
                            entity.getCreatedAt().toInstant(ZoneOffset.UTC),
                            entity.getUpdatedAt().toInstant(ZoneOffset.UTC)
                    )
            );
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to reconstitute OrderRoot from OrderEntity", e);
        }
    }

    // ── Entity → Domain (OrderItem) ─────────────────────────────────────

    /**
     * Reconstitutes an OrderItem from a JPA entity.
     * Extracts currency from the parent order entity for Money reconstruction.
     */
    default OrderItem toItemDomain(OrderProductEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            var constructor = OrderItem.class.getDeclaredConstructor(
                    ROrderItemId.class, RProductId.class, String.class,
                    RQuantity.class, RMoney.class, RMoney.class
            );
            constructor.setAccessible(true);

            String currencyCode = entity.getOrder() != null && entity.getOrder().getCurrency() != null
                    ? entity.getOrder().getCurrency()
                    : "USD";
            Currency currency = Currency.getInstance(currencyCode);

            return constructor.newInstance(
                    ROrderItemId.of(entity.getId()),
                    RProductId.of(entity.getProduct().getId()),
                    entity.getProductName(),
                    RQuantity.of(entity.getQuantity()),
                    RMoney.of(entity.getUnitPrice(), currency),
                    RMoney.of(entity.getSubtotal(), currency)
            );
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to reconstitute OrderItem from OrderProductEntity", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    /**
     * Converts Instant (domain) to LocalDateTime (JPA) using UTC zone.
     */
    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Converts java.util.Currency to its ISO 4217 currency code.
     */
    @Named("currencyToString")
    default String currencyToString(Currency currency) {
        if (currency == null) {
            return null;
        }
        return currency.getCurrencyCode();
    }

    /**
     * Creates a ProductEntity reference (proxy) from a domain ProductId for FK mapping.
     * Only the ID is set; JPA will use it for the foreign key relationship.
     */
    @Named("productIdToEntity")
    default ProductEntity productIdToEntity(RProductId productId) {
        if (productId == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity();
        entity.setId(productId.value());
        return entity;
    }
}
