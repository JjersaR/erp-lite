package com.jersa.controllers.commands;

import com.jersa.commands.product.RCreateProductCommand;
import com.jersa.commands.product.RDeactivateProductCommand;
import com.jersa.commands.product.RUpdateProductCommand;
import com.jersa.commands.product.RUpdateStockCommand;
import com.jersa.paths.ApiPaths;
import com.jersa.use_cases.product.CreateProductUseCase;
import com.jersa.use_cases.product.DeactivateProductUseCase;
import com.jersa.use_cases.product.UpdateProductUseCase;
import com.jersa.use_cases.product.UpdateStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.COMMANDS_PRODUCTS, version = "1")
@Tag(name = "Command Products", description = "Endpoints de comandos para gestión de productos")
public class CommandProductsControllerV1 {

    private final CreateProductUseCase createProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto con su información e imagen asociada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestPart(value = "product") RCreateProductCommand request,
                                              @RequestPart(value = "image") MultipartFile img) throws IOException {
        log.info("Received request to Create Product");
        String productId;
        if (img == null) {
            productId = createProductUseCase.execute(request);
        } else {
            var command = new RCreateProductCommand(
                    request.sku(),
                    request.name(),
                    request.description(),
                    request.price(),
                    request.currency(),
                    request.stock(),
                    request.categoryId(),
                    img.getBytes(),
                    img.getOriginalFilename(),
                    request.createdBy()
            );
            productId = createProductUseCase.execute(command);
        }

        return ResponseEntity.created(URI.create(ApiPaths.COMMANDS_PRODUCTS + "/" + productId)).build();
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza la información e imagen de un producto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> putProduct(@PathVariable String id, @Valid @RequestPart(value = "product") RUpdateProductCommand request,
                                           @RequestPart(value = "img") MultipartFile img) throws IOException {
        log.info("Updating product with id {}", id);
        var command = new RUpdateProductCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.categoryId(),
                img != null ? img.getBytes() : null,
                img != null ? img.getOriginalFilename() : null
        );

        this.updateProductUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar producto", description = "Marca un producto como inactivo dado su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto desactivado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> patchProductDeactivate(@PathVariable String id) {
        log.info("Deactivating product {}", id);
        var command = new RDeactivateProductCommand(id);

        this.deactivateProductUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar stock de producto", description = "Modifica la cantidad en inventario de un producto indicando el motivo del ajuste")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de stock inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> patchProductStock(@PathVariable String id, @Valid @RequestBody RUpdateStockCommand request) {
        log.info("Updating stock product {}", id);
        var command = new RUpdateStockCommand(id, request.quantity(), request.reason());

        this.updateStockUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}