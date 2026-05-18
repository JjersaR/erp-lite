package com.jersa.persistence.mongo.adapters;

import com.jersa.persistence.mongo.mappers.IProductCatalogMapper;
import com.jersa.persistence.mongo.repositories.IProductInCatalogDocumentRepository;
import com.jersa.ports.repositories.IProductCatalogRepositoryPort;
import com.jersa.views.RProductView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static com.jersa.constants.CacheConstants.*;

@Slf4j
@Repository
@AllArgsConstructor
public class ProductCatalogRepositoryAdapter implements IProductCatalogRepositoryPort {
    private final IProductInCatalogDocumentRepository productInCatalogDocumentRepository;
    private final IProductCatalogMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<RProductView> findById(String id) {
        log.info("Finding product by id {}", id);

        Object raw = this.redisTemplate.opsForValue().get(CACHE_PRODUCTS_BY_ID + id);

        if (raw != null) {
            log.info("Found product by id in cache {}", id);
            return Optional.of(this.objectMapper.convertValue(raw, RProductView.class));
        }
        return this.productInCatalogDocumentRepository.findById(id).map(mapper::toView);
    }

    @Override
    public Optional<RProductView> findBySKU(String sku) {
        log.info("Finding product by sku {}", sku);

        Object raw = this.redisTemplate.opsForValue().get(CACHE_PRODUCTS_BY_SKU + sku);

        if (raw != null) {
            log.info("Found product by sku in cache {}", sku);
           return Optional.of(this.objectMapper.convertValue(raw, RProductView.class));
        }
        return this.productInCatalogDocumentRepository.findBySku(sku).map(mapper::toView);
    }

    @Override
    public List<RProductView> findByText(String text) {
        log.info("Finding product by text {}", text);
        return this.productInCatalogDocumentRepository.findByTextAndActive(text).stream().map(mapper::toView).toList();
    }

    @Override
    public List<RProductView> findByCategory(String category) {
        log.info("Finding product by category {}", category);

        Object raw = this.redisTemplate.opsForList().range(CACHE_PRODUCTS_BY_CATEGORY + category, 0, -1);

        if (raw != null) {
            log.info("Found product by category in cache {}", category);
            return this.objectMapper.convertValue(raw, this.objectMapper.getTypeFactory().constructCollectionType(List.class, RProductView.class));
        }
        return this.productInCatalogDocumentRepository.findByCategoryIdAndActiveTrue(category).stream().map(mapper::toView).toList();
    }

    @Override
    public List<RProductView> findActive() {
        log.info("Finding product by active true");
        Object raw = this.redisTemplate.opsForList().range(CACHE_PRODUCTS_ACTIVE, 0, -1);

        if (raw != null) {
            log.info("Found product by active in cache");
            return this.objectMapper.convertValue(raw, this.objectMapper.getTypeFactory().constructCollectionType(List.class, RProductView.class));
        }
        return this.productInCatalogDocumentRepository.findByActiveTrueOrderByIdAsc().stream().map(mapper::toView).toList();
    }
}
