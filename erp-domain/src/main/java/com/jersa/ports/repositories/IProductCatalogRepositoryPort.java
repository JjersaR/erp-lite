package com.jersa.ports.repositories;

import com.jersa.views.RProductView;

import java.util.List;
import java.util.Optional;

public interface IProductCatalogRepositoryPort {
    Optional<RProductView> findById(String id);

    Optional<RProductView> findBySKU(String sku);

    List<RProductView> findByText(String text);

    List<RProductView> findByCategory(String category);

    List<RProductView> findActive();
}