package com.jersa.queries.product;

import com.jersa.exceptions.QueryException;
import com.jersa.ports.repositories.IProductCatalogRepositoryPort;
import com.jersa.views.RProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindProductBySkuQuery {
    private final IProductCatalogRepositoryPort repository;

    public Optional<RProductView> execute(String sku) {
        log.info("Executing FindProductBySkuQuery");
        try {
            return this.repository.findBySKU(sku);
        } catch (RuntimeException e) {
            throw new QueryException("Error executing FindProductBySkuQuery");
        }
    }
}
