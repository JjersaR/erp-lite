package com.jersa.persistence.jpa.adapters;

import com.jersa.entities.product.ProductRoot;
import com.jersa.entities.product.RProductId;
import com.jersa.persistence.jpa.entities.OrderEntity;
import com.jersa.persistence.jpa.mappers.IProductJpaMapper;
import com.jersa.persistence.jpa.repositories.IProductEntityRepository;
import com.jersa.ports.repositories.IProductRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class ProductRepositoryAdapter implements IProductRepositoryPort {

    private final IProductEntityRepository repository;
    private final IProductJpaMapper mapper;

    @Override
    public ProductRoot save(ProductRoot productRoot) {
        log.info("Saving product {}", productRoot);
        try {
            var entity = this.mapper.toEntity(productRoot);
            entity.setId(productRoot.getId().value());

            log.info("try to saving product {}", entity.getSku());

            var saved = this.repository.save(entity);

            log.info("save product successfully.");
            return this.mapper.toDomain(saved);
        } catch (Exception e) {
            log.error("Error while saving product into repository", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<ProductRoot> findById(RProductId productId) {
        log.info("Finding all Product Root by id {}", productId.value());
        try {
            var entityOpt = this.repository.findById(productId.value());

            if (entityOpt.isEmpty()) {
                log.info("No Product Root found with id {}", productId.value());
                return Optional.empty();
            }

            return Optional.of(this.mapper.toDomain(entityOpt.get()));
        } catch (Exception e) {
            log.error("Error on finding by id, ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ProductRoot> findBySKU(String sku) {
        log.info("Finding all Product Root by sku {}", sku);
        try {
            var entityOpt = this.repository.findBySku(sku);

            if (entityOpt.isEmpty()) {
                log.info("No Product Root found with sku {}", sku);
                return Optional.empty();
            }

            return Optional.of(this.mapper.toDomain(entityOpt.get()));
        } catch (Exception e) {
            log.error("Error on finding by sku, ", e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(ProductRoot productRoot) {
        log.info("Deleting Product Root {}", productRoot);
        try {
            this.repository.deleteById(productRoot.getId().value());
            log.info("Deleted Product Root successfully.");
        } catch (Exception e) {
            log.error("Error on deleting this Product Root, ", e);
            throw new IllegalStateException(e);
        }
    }
}
