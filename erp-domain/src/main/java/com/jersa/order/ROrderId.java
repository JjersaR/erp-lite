package com.jersa.order;

import java.util.UUID;

public record ROrderId(UUID value) {
  public ROrderId {
    if (value == null) {
      throw new IllegalArgumentException("Order ID cannot be null");
    }
  }

  /**
   * Creates an OrderId from a UUID value.
   *
   * @param value the UUID value
   * @return a new OrderId instance
   */
  public static ROrderId of(UUID value) {
    return new ROrderId(value);
  }

  /**
   * Generates a new random OrderId.
   *
   * @return a new OrderId instance with a random UUID
   */
  public static ROrderId generate() {
    return new ROrderId(UUID.randomUUID());
  }
}
