package com.jersa.use_cases.product;

import com.jersa.commands.product.RCreateProductCommand;
import com.jersa.entities.product.*;
import com.jersa.exceptions.CommandException;
import com.jersa.ports.repositories.IProductRepositoryPort;
import com.jersa.ports.services.IImageStorageServicePort;
import com.jersa.shared.RMoney;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

/**
 * Use Case: Create a new product.
 * JIRA TICKET: ERP-6735
 * 1. Validate SKU uniqueness
 * 2. Upload image to S3 (if provided)
 * 3. Create ProductRoot aggregate
 * 4. Persist to PostgreSQL
 * 5. Publish ProductCreated event (CQRS sync to MongoDB)
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {
    private final IProductRepositoryPort productRepository;
    private final IImageStorageServicePort imageStorageService;

    public String execute(RCreateProductCommand command) {
        log.info("Creating product with SKU: {}", command.sku());

        try {
            // 1. Validate SKU uniqueness
            validateSkuUniqueness(command.sku());

            // 2. Upload image (if provided)
            var image = uploadImage(command);

            // 3. Create value objects
            RSKU sku = RSKU.of(command.sku());
            RProductName name = RProductName.of(command.name());
            RMoney price = RMoney.of(command.price(), Currency.getInstance(command.currency()));
            RStock stock = RStock.of(command.stock());
            RCategoryReference category = RCategoryReference.of(command.categoryId());

            // 4. Create product aggregate
            ProductRoot product = ProductRoot.create(
                    sku,
                    name,
                    command.description(),
                    price,
                    stock,
                    category,
                    image,
                    command.createdBy()
            );

            log.debug("Product created in domain with ID: {}", product.getId().value());

            // 5. Persist product
            ProductRoot savedProduct = productRepository.save(product);

            log.info("Product persisted with ID: {}", savedProduct.getId().value());

            // TODO: Handle domain events - Sync to MongoDB

            return savedProduct.getId().value().toString();

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data for product creation");
            throw new CommandException("Error creating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating product", e);
            throw new CommandException("Failed to create product: " + e.getMessage());
        }
    }

    private void validateSkuUniqueness(String sku) {
        log.debug("Validating SKU uniqueness: {}", sku);

        if (productRepository.findBySKU(sku).isPresent()) {
            log.warn("SKU already exists: {}", sku);
            throw new CommandException("Product with SKU '" + sku + "' already exists");
        }
    }

    private RProductImage uploadImage(RCreateProductCommand command) {
        if (!command.hasImage()) {
            log.debug("No image update requested");
            return null;
        }

        log.info("Uploading image with SKU: {}", command.sku());

        try {
            // Upload new image
            return imageStorageService.upload(
                    command.imageName(),
                    command.imageData()
            );
        } catch (Exception e) {
            log.error("Failed to upload new image", e);
            throw new CommandException("Failed to upload product image: " + e.getMessage());
        }

    }
}
