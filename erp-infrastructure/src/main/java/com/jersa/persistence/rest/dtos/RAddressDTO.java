package com.jersa.persistence.rest.dtos;

public record RAddressDTO(
        String street,
        String suite,
        String city,
        String zipcode,
        RGeoDTO geo
) {
}