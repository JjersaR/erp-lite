package com.jersa.commands.order;

import jakarta.validation.constraints.*;

/**
 * Command to cancel an order.
 * Can be used to cancel orders in PENDING or CONFIRMED status.
 * If order was CONFIRMED, stock will be released back to inventory.
 *
 * @param orderId Order ID to cancel
 * @param reason  Cancellation reason (for audit purposes)
 */
public record RCancelOrderCommand(
        String orderId,

        @NotBlank(message = "Cancellation reason cannot be null or blank")
        @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
        String reason
) {
    /**
     * Custom validation in compact constructor
     */
    public RCancelOrderCommand {
        if (reason != null && reason.trim().length() < 10) {
            throw new IllegalArgumentException("Cancellation reason must have at least 10 meaningful characters");
        }
    }

    /**
     * Check if reason mentions customer request
     */
    public boolean isCustomerRequest() {
        return reason != null &&
                (reason.toLowerCase().contains("customer") ||
                        reason.toLowerCase().contains("client"));
    }

    /**
     * Check if reason mentions stock issues
     */
    public boolean isStockIssue() {
        return reason != null &&
                (reason.toLowerCase().contains("stock") ||
                        reason.toLowerCase().contains("inventory") ||
                        reason.toLowerCase().contains("out of stock"));
    }
}
