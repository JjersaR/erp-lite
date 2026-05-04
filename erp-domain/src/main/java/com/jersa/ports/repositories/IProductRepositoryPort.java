package com.jersa.ports.repositories;

import com.jersa.entities.product.ProductRoot;
import com.jersa.entities.product.RProductId;

import java.util.Optional;

public interface IProductRepositoryPort {
    ProductRoot save(ProductRoot productRoot);

    Optional<ProductRoot> findById(RProductId productId);

    Optional<ProductRoot> findBySKU(String sku);

    void delete(ProductRoot productRoot);
}
