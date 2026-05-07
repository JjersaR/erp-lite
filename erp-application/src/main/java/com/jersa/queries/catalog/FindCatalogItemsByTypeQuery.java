package com.jersa.queries.catalog;

import com.jersa.enums.ECatalogType;
import com.jersa.ports.repositories.ICatalogRepositoryPort;
import com.jersa.views.RItemsView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindCatalogItemsByTypeQuery {
    private final ICatalogRepositoryPort repository;

    public List<RItemsView> execute(ECatalogType type) {
        log.info("Executing FindCatalogItemsByTypeQuery");
        return this.repository.findItemsByType(type);
    }
}
