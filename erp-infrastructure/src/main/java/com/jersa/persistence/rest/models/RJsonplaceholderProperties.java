package com.jersa.persistence.rest.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jsonplaceholder.api")
public record RJsonplaceholderProperties(

        @NotBlank
        String baseUrl,

        @NotBlank
        String usersEndpoint,

        @Min(1)
        int connectTimeout,

        @Min(1)
        int readTimeout,

        @NotNull
        Boolean enabled

) {}
