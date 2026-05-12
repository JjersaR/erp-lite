package com.jersa.controllers.queries;

import com.jersa.dtos.BaseResponseWrapper;
import com.jersa.exceptions.QueryException;
import com.jersa.paths.ApiPaths;
import com.jersa.queries.product.*;
import com.jersa.views.RProductView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.QUERIES_PRODUCTS, version = "1")
@Tag(name = "Query Products", description = "Endpoints de consulta para productos")
public class QueryProductsControllerV1 {

    private final FindProductByIdQuery findProductByIdQuery;
    private final FindProductByCategoryQuery findProductByCategoryQuery;
    private final FindProductByActiveQuery findProductByActiveQuery;
    private final FindProductBySkuQuery findProductBySkuQuery;
    private final FindProductByTextQuery findProductByTextQuery;

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto dado su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseWrapper<RProductView>> getById(@PathVariable String id) {
        log.info("GET product by id: {}", id);
        var response = this.findProductByIdQuery.execute(id).orElseThrow(() -> new QueryException("Product by id not found"));
        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener producto por SKU", description = "Retorna un producto dado su código SKU")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "sku")
    public ResponseEntity<BaseResponseWrapper<RProductView>> getBySku(@RequestParam String sku) {
        log.info("GET product by SKU: {}", sku);
        var response = this.findProductBySkuQuery.execute(sku).orElseThrow(() -> new QueryException("Product by SKU not found"));
        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener productos activos", description = "Retorna la lista de todos los productos activos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos activos"),
            @ApiResponse(responseCode = "204", description = "No hay productos activos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/active")
    public ResponseEntity<BaseResponseWrapper<List<RProductView>>> getByActive() {
        log.info("GET products actives");
        var response = this.findProductByActiveQuery.execute();
        return (response.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Buscar productos por texto", description = "Retorna productos cuyo nombre o descripción coincida con el texto de búsqueda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/search", params = "text")
    public ResponseEntity<BaseResponseWrapper<List<RProductView>>> getByText(@RequestParam String text) {
        log.info("GET product by text: {}", text);
        var response = this.findProductByTextQuery.execute(text);
        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener productos por categoría", description = "Retorna todos los productos que pertenecen a la categoría indicada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos por categoría"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "category")
    public ResponseEntity<BaseResponseWrapper<List<RProductView>>> getByCategory(@RequestParam String category) {
        log.info("GET product by category: {}", category);
        var response = this.findProductByCategoryQuery.execute(category);
        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }
}