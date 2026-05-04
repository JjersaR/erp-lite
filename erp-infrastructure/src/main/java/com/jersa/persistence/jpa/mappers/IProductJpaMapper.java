package com.jersa.persistence.jpa.mappers;

import com.jersa.entities.product.*;
import com.jersa.persistence.jpa.entities.ProductEntity;
import com.jersa.shared.RAuditInfo;
import com.jersa.shared.RMoney;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;

/**
 * Anti-Corruption Layer between ProductRoot (Domain) and ProductEntity (JPA).
 * Handles value object unwrapping/wrapping and Instant ←→ LocalDateTime conversion.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IProductJpaMapper {
    // ── Domain → Entity ─────────────────────────────────────────────────
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "sku.value", target = "sku")
    @Mapping(source = "name.value", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price.amount", target = "price")
    @Mapping(source = "stock.value", target = "stock")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "image.imageUrl", target = "imageUrl")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "auditInfo.createdAt", target = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "auditInfo.updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDateTime")
    ProductEntity toEntity(ProductRoot domain);

    // ── Entity → Domain ─────────────────────────────────────────────────

    /**
     * Reconstitutes a ProductRoot from a JPA entity.
     * Uses reflection to access the private constructor since domain aggregates
     * enforce encapsulated construction via factory methods.
     */
    default ProductRoot toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            var constructor = ProductRoot.class.getDeclaredConstructor(
                    RProductId.class, RSKU.class, RProductName.class, String.class,
                    RMoney.class, RStock.class, RCategoryReference.class, RProductImage.class,
                    boolean.class, RAuditInfo.class
            );
            constructor.setAccessible(true);

            return constructor.newInstance(
                    RProductId.of(entity.getId()),
                    RSKU.of(entity.getSku()),
                    RProductName.of(entity.getName()),
                    entity.getDescription(),
                    RMoney.of(entity.getPrice(), Currency.getInstance("USD")),
                    RStock.of(entity.getStock()),
                    entity.getCategoryId() != null ? RCategoryReference.of(entity.getCategoryId()) : null,
                    entity.getImageUrl() != null ? RProductImage.of(entity.getImageUrl()) : null,
                    entity.getActive(),
                    new RAuditInfo(
                            "system",
                            entity.getCreatedAt().toInstant(ZoneOffset.UTC),
                            entity.getUpdatedAt().toInstant(ZoneOffset.UTC)
                    )
            );
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to reconstitute ProductRoot from ProductEntity", e);
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
}
