package com.jersa.persistence.mongo.adapters;

import com.jersa.enums.ECatalogType;
import com.jersa.persistence.mongo.mappers.ICatalogMapper;
import com.jersa.persistence.mongo.repositories.ICatalogRepository;
import com.jersa.ports.repositories.ICatalogRepositoryPort;
import com.jersa.views.RCatalogView;
import com.jersa.views.RItemsView;
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
public class CatalogRepositoryAdapter implements ICatalogRepositoryPort {
    private final ICatalogRepository repository;
    private final ICatalogMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<RCatalogView> findByType(ECatalogType type) {
        log.info("Finding by type {}", type);

        Object raw = this.redisTemplate.opsForValue().get(CACHE_CATALOGS_BY_TYPE + type.toString());

        if (raw != null) {
            log.info("Cache found by type {}", type);
            return Optional.of(this.objectMapper.convertValue(raw, RCatalogView.class));
        }
        return this.repository.findByCatalogType(type).map(mapper::toView);
    }

    @Override
    public List<RItemsView> findItemsByType(ECatalogType type) {
        log.info("Finding Items by type {}", type);

        Object raw = this.redisTemplate.opsForValue().get(CACHE_CATALOGS_BY_TYPE + type.name());

        if (raw != null) {
            log.info("Cache found Items by type {}", type);
            var cached = this.objectMapper.convertValue(raw, RCatalogView.class);
            return cached.items();
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
