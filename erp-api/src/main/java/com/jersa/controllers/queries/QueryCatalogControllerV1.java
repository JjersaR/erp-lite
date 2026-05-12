package com.jersa.controllers.queries;

import com.jersa.dtos.BaseResponseWrapper;
import com.jersa.enums.ECatalogType;
import com.jersa.exceptions.QueryException;
import com.jersa.paths.ApiPaths;
import com.jersa.queries.catalog.FindCatalogByTypeQuery;
import com.jersa.queries.catalog.FindCatalogItemByCodeQuery;
import com.jersa.queries.catalog.FindCatalogItemsByTypeQuery;
import com.jersa.views.RCatalogView;
import com.jersa.views.RItemsView;
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
@RequestMapping(path = ApiPaths.QUERIES_CATALOGS, version = "1")
@Tag(name = "Query Catalogs", description = "Endpoints de consulta para catálogos e ítems")
public class QueryCatalogControllerV1 {

    private final FindCatalogByTypeQuery findCatalogByTypeQuery;
    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;
    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    @Operation(summary = "Obtener catálogo por tipo", description = "Retorna el catálogo correspondiente al tipo indicado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Catálogo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{type}")
    public ResponseEntity<BaseResponseWrapper<RCatalogView>> getByType(@PathVariable String type) {
        log.info("GET catalog by type: {}", type);

        var catalogType = ECatalogType.valueOf(type.toUpperCase());

        RCatalogView response = this.findCatalogByTypeQuery.execute(catalogType)
                .orElseThrow(() -> new QueryException("Catalog with type " + type + " not found"));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítems de un catálogo por tipo", description = "Retorna todos los ítems del catálogo del tipo especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ítems del catálogo")
    })
    @GetMapping("/{type}/items")
    public ResponseEntity<BaseResponseWrapper<List<RItemsView>>> getItemsByType(@PathVariable String type) {
        log.info("GET catalog items by type: {}", type);

        var catalogType = ECatalogType.valueOf(type.toUpperCase());

        var response = this.findCatalogItemsByTypeQuery.execute(catalogType);

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítem de catálogo por tipo y código", description = "Retorna un ítem específico del catálogo filtrando por tipo y código")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping(path = "/{type}/items", params = "code")
    public ResponseEntity<BaseResponseWrapper<RItemsView>> getItemByTypeAndCode(@PathVariable String type, @RequestParam String code) {
        log.info("GET catalog item by type: {} and code: {}", type, code);

        var catalogType = ECatalogType.valueOf(type.toUpperCase());

        var response = this.findCatalogItemByCodeQuery.execute(catalogType, code)
                .orElseThrow(() -> new QueryException("Item with code " + code + " not found for type " + type));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }
}