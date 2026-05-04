package com.jersa.ports.repositories;

import java.util.List;
import java.util.Optional;

import com.jersa.entities.catalog.CatalogRoot;
import com.jersa.entities.catalog.CatalogItem;
import com.jersa.entities.catalog.ECatalogType;

public interface ICatalogRepositoryPort {

  Optional<CatalogRoot> findByType(ECatalogType type);

  List<CatalogItem> findItemsByType(ECatalogType type);

  Optional<CatalogItem> findItemByTypeAndCode(ECatalogType type, String code);
}
