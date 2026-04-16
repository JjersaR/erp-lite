package com.jersa.product;

import java.util.UUID;

public record RProductId(UUID value) {
  public RProductId {
    if (value == null) {
      throw new IllegalArgumentException("Product ID cannot be null");
    }
  }

  /**
   * Creates a ProductId from a UUID value.
   *
   * @param value the UUID value
   * @return a new ProductId instance
   */
  public static RProductId of(UUID value) {
    return new RProductId(value);
  }

  /**
   * Generates a new random ProductId.
   *
   * @return a new ProductId instance with a random UUID
   */
  public static RProductId generate() {
    return new RProductId(UUID.randomUUID());
  }
}
