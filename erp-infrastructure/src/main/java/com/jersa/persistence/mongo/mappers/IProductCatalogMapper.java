package com.jersa.persistence.mongo.mappers;

import com.jersa.persistence.mongo.documents.ProductInCatalogDocument;
import com.jersa.views.RProductView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IProductCatalogMapper {

    @Mapping(source = "currency", target = "money")
    RProductView toView(ProductInCatalogDocument document);
}
