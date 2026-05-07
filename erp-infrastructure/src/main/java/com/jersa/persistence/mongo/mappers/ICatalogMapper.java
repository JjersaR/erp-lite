package com.jersa.persistence.mongo.mappers;

import com.jersa.persistence.mongo.documents.CatalogDocument;
import com.jersa.persistence.mongo.documents.RCatalogItem;
import com.jersa.views.RCatalogView;
import com.jersa.views.RItemsView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ICatalogMapper {

    @Mapping(source = "catalogType", target = "type")
    RCatalogView toView(CatalogDocument document);

    RItemsView toItemView(RCatalogItem item);
}
