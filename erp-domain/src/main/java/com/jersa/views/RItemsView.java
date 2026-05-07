package com.jersa.views;

/**
 * Read model representation of an Items.
 * Used for queries (CQRS read side).
 * This is a simplified view optimized for display.
 */
public record RItemsView(
        String code,
        String value,
        String description,
        Integer displayOrder
) {
}
