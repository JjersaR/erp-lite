package com.jersa.persistence.rest.dtos;

public record RUserDTO(Long id, String name, String username, String email, RAddressDTO address, String phone,
                       String website, RCompanyDTO company) {
}