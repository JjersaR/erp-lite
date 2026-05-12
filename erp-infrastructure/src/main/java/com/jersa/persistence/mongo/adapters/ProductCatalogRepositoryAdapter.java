package com.jersa.persistence.mongo.adapters;

import com.jersa.persistence.mongo.mappers.IProductCatalogMapper;
import com.jersa.persistence.mongo.repositories.IProductInCatalogDocumentRepository;
import com.jersa.ports.repositories.IProductCatalogRepositoryPort;
import com.jersa.views.RProductView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.jersa.constants.CacheConstants.*;

@Slf4j
@Repository
@AllArgsConstructor
public class ProductCatalogRepositoryAdapter implements IProductCatalogRepositoryPort {
    private final IProductInCatalogDocumentRepository productInCatalogDocumentRepository;
    private final IProductCatalogMapper mapper;
    private final CacheManager cacheManager;

    @Override
    public Optional<RProductView> findById(String id) {
        log.info("Finding product by id {}", id);

        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_ID);

        if (cache != null) {
            RProductView productCache = cache.get(id, RProductView.class);
            if (productCache != null) {
                log.info("Found product in cache by id");
                return Optional.of(productCache);
            }
        }
        return this.productInCatalogDocumentRepository.findById(id).map(mapper::toView);
    }

    @Override
    public Optional<RProductView> findBySKU(String sku) {
        log.info("Finding product by sku {}", sku);

        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_SKU);

        if (cache != null) {
            RProductView productCache = cache.get(sku, RProductView.class);
            if (productCache != null) {
                log.info("Found product in cache by sku");
                return Optional.of(productCache);
            }
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

        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_CATEGORY);

        if (cache != null) {
            List<RProductView> productsCache = cache.get(category, List.class);
            if (productsCache != null) {
                log.info("Found products in cache");
                return productsCache;
            }
        }
        return this.productInCatalogDocumentRepository.findByCategoryIdAndActiveTrue(category).stream().map(mapper::toView).toList();
    }

    @Override
    public List<RProductView> findActive() {
        log.info("Finding product by active true");
        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_ACTIVE);

        if (cache != null) {
            List<RProductView> productsCache = cache.get("all", List.class);
            if (productsCache != null) {
                log.info("Found active products in cache");
                return productsCache;
            }
        }
        return this.productInCatalogDocumentRepository.findByActiveTrueOrderByIdAsc().stream().map(mapper::toView).toList();
    }
}
