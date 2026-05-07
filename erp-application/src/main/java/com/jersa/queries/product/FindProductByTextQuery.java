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
public class FindProductByTextQuery {
    private final IProductCatalogRepositoryPort repository;

    public List<RProductView> execute(String text) {
        log.info("Executing FindProductByTextQuery");
        return this.repository.findByText(text);
    }
}
