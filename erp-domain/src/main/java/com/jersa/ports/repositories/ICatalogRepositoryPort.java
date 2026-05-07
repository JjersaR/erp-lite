package com.jersa.ports.repositories;

import com.jersa.enums.ECatalogType;
import com.jersa.views.RCatalogView;
import com.jersa.views.RItemsView;

import java.util.List;
import java.util.Optional;

public interface ICatalogRepositoryPort {

  Optional<RCatalogView> findByType(ECatalogType type);

  List<RItemsView> findItemsByType(ECatalogType type);

  Optional<RItemsView> findItemByTypeAndCode(ECatalogType type, String code);
}
