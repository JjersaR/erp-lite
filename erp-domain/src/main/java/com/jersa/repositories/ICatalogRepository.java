package com.jersa.repositories;

import java.util.List;
import java.util.Optional;

import com.jersa.catalog.Catalog;
import com.jersa.catalog.CatalogItem;
import com.jersa.catalog.ECatalogType;

public interface ICatalogRepository {

  Optional<Catalog> findByType(ECatalogType type);

  List<CatalogItem> findItemsByType(ECatalogType type);

  Optional<CatalogItem> findItemByTypeAndCode(ECatalogType type, String code);
}
