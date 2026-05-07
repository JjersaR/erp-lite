package com.jersa.queries.product;

import com.jersa.ports.repositories.IProductCatalogRepositoryPort;
import com.jersa.views.RProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindProductByActiveQuery {
    private final IProductCatalogRepositoryPort repository;

    public List<RProductView> execute() {
        log.info("Executing FindProductByActiveQuery");
        return this.repository.findActive();
    }
}
