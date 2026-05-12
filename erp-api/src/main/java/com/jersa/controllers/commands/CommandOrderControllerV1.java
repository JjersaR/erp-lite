package com.jersa.controllers.commands;

import com.jersa.commands.order.RCancelOrderCommand;
import com.jersa.commands.order.RCreateOrderCommand;
import com.jersa.commands.order.RUpdateOrderStatusCommand;
import com.jersa.paths.ApiPaths;
import com.jersa.use_cases.order.CancelOrderUseCase;
import com.jersa.use_cases.order.CreateOrderUseCase;
import com.jersa.use_cases.order.UpdateOrderStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.COMMANDS_ORDERS, version = "1")
@Tag(name = "Command Orders", description = "Endpoints de comandos para gestión de órdenes")
public class CommandOrderControllerV1 {
    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @Operation(summary = "Crear orden", description = "Crea una nueva orden de compra en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de orden inválidos"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody RCreateOrderCommand request) {
        log.info("Received request to create order");

        String id = this.createOrderUseCase.execute(request);

        return ResponseEntity.created(URI.create(ApiPaths.COMMANDS_ORDERS + "/" + id)).build();
    }

    @Operation(summary = "Cancelar orden", description = "Cancela una orden existente indicando el motivo de la cancelación")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> patchOrderCancel(@PathVariable String id, @RequestParam String reason) {
        log.info("Received request to cancel order");

        var command = new RCancelOrderCommand(id, reason);

        this.cancelOrderUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar estado de orden", description = "Cambia el estado actual de una orden (ej. PENDING, SHIPPED, DELIVERED)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estado de orden actualizado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio"),
            @ApiResponse(responseCode = "422", description = "Error al procesar el comando"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> patchOrderStatus(@PathVariable String id, @RequestParam String status) {
        log.info("Received request to update order status");

        var command = new RUpdateOrderStatusCommand(id, status);

        this.updateOrderStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}