package com.jersa.persistence.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RCompanyDTO(String name, @JsonProperty("catchPhrase") String cp, String bs) {
}