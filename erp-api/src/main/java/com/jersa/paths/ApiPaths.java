package com.jersa.paths;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiPaths {
    public static final String COMMANDS_PRODUCTS = "/api/commands/products";
    public static final String COMMANDS_ORDERS = "/api/commands/orders";
    public static final String QUERIES_PRODUCTS = "/api/queries/products";
    public static final String QUERIES_CATALOGS = "/api/queries/catalogs";
}
