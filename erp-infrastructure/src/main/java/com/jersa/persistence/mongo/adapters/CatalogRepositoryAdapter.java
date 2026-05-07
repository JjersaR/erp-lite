package com.jersa.persistence.mongo.adapters;

import com.jersa.enums.ECatalogType;
import com.jersa.persistence.mongo.mappers.ICatalogMapper;
import com.jersa.persistence.mongo.repositories.ICatalogRepository;
import com.jersa.ports.repositories.ICatalogRepositoryPort;
import com.jersa.views.RCatalogView;
import com.jersa.views.RItemsView;
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
public class CatalogRepositoryAdapter implements ICatalogRepositoryPort {
    private final ICatalogRepository repository;
    private final ICatalogMapper mapper;
    private final CacheManager cacheManager;

    @Override
    public Optional<RCatalogView> findByType(ECatalogType type) {
        log.info("Finding by type {}", type);

        Cache cache = cacheManager.getCache(CACHE_CATALOGS_BY_TYPE);
        if (cache != null) {
            RCatalogView catalogCache = cache.get(type.name(), RCatalogView.class);

            if (catalogCache != null) {
                log.info("Found catalog in cache found: {} ", catalogCache);
                return Optional.of(catalogCache);
            }
        }
        return this.repository.findByCatalogType(type).map(mapper::toView);
    }

    @Override
    public List<RItemsView> findItemsByType(ECatalogType type) {
        log.info("Finding Items by type {}", type);

        Cache cache = cacheManager.getCache(CACHE_CATALOGS_ITEMS);

        if (cache != null) {
            List<RItemsView> itemsCache = cache.get(type.name(), List.class);

            if (itemsCache != null) {
                log.info("Found items in cache found, total : {} ", itemsCache.size());
                return itemsCache;
            }
        }

        return this.repository.findByCatalogType(type).map(doc -> doc.getItems().stream().map(mapper::toItemView).toList()).orElse(List.of());
    }

    @Override
    public Optional<RItemsView> findItemByTypeAndCode(ECatalogType type, String code) {
        log.info("Finding Items by type {} and code {}", type, code);
        return this.repository.findByCatalogType(type).flatMap(doc -> doc.getItems().stream().
                filter(item -> item.code().equals(code)).findFirst().map(mapper::toItemView));
    }
}
