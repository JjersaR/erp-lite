package com.jersa.order;

import java.util.UUID;

public record ROrderItemId(UUID value) {
  public ROrderItemId {
    if (value == null) {
      throw new IllegalArgumentException("OrderItem ID cannot be null");
    }
  }

  /**
   * Creates an OrderItemId from a UUID value.
   *
   * @param value the UUID value
   * @return a new OrderItemId instance
   */
  public static ROrderItemId of(UUID value) {
    return new ROrderItemId(value);
  }

  /**
   * Generates a new random OrderItemId.
   *
   * @return a new OrderItemId instance with a random UUID
   */
  public static ROrderItemId generate() {
    return new ROrderItemId(UUID.randomUUID());
  }
}
