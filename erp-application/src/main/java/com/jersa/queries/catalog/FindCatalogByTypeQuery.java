package com.jersa.queries.catalog;

import com.jersa.enums.ECatalogType;
import com.jersa.ports.repositories.ICatalogRepositoryPort;
import com.jersa.views.RCatalogView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindCatalogByTypeQuery {
    private final ICatalogRepositoryPort repository;

    public Optional<RCatalogView> execute(ECatalogType type) {
        log.info("Executing FindProductByTypeQuery");
        return this.repository.findByType(type);
    }
}