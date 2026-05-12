package com.jersa.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.useRequestHeader("X-Api-Version").setDefaultVersion("1"); // header
        // configurer.usePathSegment(1); // /api(segmento 0)/v1(segmento 1)/products(segmento 2)
    }
}
