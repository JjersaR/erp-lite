package com.jersa.queries.catalog;

import com.jersa.enums.ECatalogType;
import com.jersa.ports.repositories.ICatalogRepositoryPort;
import com.jersa.views.RItemsView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindCatalogItemByCodeQuery {
    private final ICatalogRepositoryPort repository;

    public Optional<RItemsView> execute(ECatalogType type, String code) {
        log.info("Executing FindCatalogItemByCodeQuery");
        return this.repository.findItemByTypeAndCode(type, code);
    }
}
