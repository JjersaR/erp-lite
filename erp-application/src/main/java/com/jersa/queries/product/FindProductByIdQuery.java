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
public class FindProductByIdQuery {
    private final IProductCatalogRepositoryPort repository;

    public Optional<RProductView> execute(String id) {
        log.info("Executing FindProductByIdQuery");
        try {
            return this.repository.findById(id);
        } catch (RuntimeException e) {
            throw new QueryException("Error executing FindProductByIdQuery");
        }
    }
}
