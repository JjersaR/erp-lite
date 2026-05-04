package com.jersa.commands.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RCreateProductCommand(
        @NotBlank(message = "SKU cannot be null or blank") String sku,
        @NotBlank(message = "Product name cannot be null or blank") @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters") String name,
        @Size(max = 1000, message = "Description cannot exceed 1000 characters") String description,
        @NotNull(message = "Price cannot be null") @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0") @Digits(integer = 13, fraction = 2, message = "Price format invalid (max 13 digits, 2 decimals)") BigDecimal price,
        @NotBlank(message = "Currency cannot be null or blank") @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., USD, MXN, EUR)") String currency,
        @NotNull(message = "Stock cannot be null") @Min(value = 0, message = "Stock cannot be negative") Integer stock,
        @NotBlank(message = "Category ID cannot be null or blank") String categoryId,
        byte[] imageData,
        String imageName,
        @NotBlank(message = "Created by cannot be null or blank") String createdBy) {

    /**
     * Custom validation: If imageData is provided, imageName must also be provided
     */
    public RCreateProductCommand {
        if (imageData != null && imageData.length > 0 &&
                (imageName == null || imageName.isBlank())) {
            throw new IllegalArgumentException("Image name is required when image data is provided");
        }
    }

    /**
     * Check if this command includes an image upload.
     */
    public boolean hasImage() {
        return imageData != null && imageData.length > 0;
    }
}
