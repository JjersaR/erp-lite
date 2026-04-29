package com.jersa.persistence.rest.mappers;

import com.jersa.customer.RCustomerInfo;
import com.jersa.persistence.rest.dtos.RAddressDTO;
import com.jersa.persistence.rest.dtos.RUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ICustomerInfoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "address", target = "address", qualifiedByName = "formatFullAddress")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.zipcode", target = "zipcode")
    @Mapping(source = "company.name", target = "companyName")
    RCustomerInfo toCustomerInfo(RUserDTO dto);

    @Named("formatFullAddress")
    default String formatFullAddress(RAddressDTO address) {
        if (address == null || address.street() == null) return null;

        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(address.street());

        if (address.suite() != null && !address.suite().isBlank()) {
            fullAddress.append(", ").append(address.suite());
        }

        return fullAddress.toString();
    }
}
