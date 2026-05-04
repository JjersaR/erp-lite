package com.jersa.commands.product;

import jakarta.validation.constraints.NotBlank;

public record RDeactivateProductCommand(
        @NotBlank(message = "Product ID cannot be null or blank")
        String productId
) {
}
