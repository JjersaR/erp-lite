package com.jersa.shared;

public record RQuantity(Integer value) {
  public RQuantity {
    if (value == null) {
      throw new IllegalArgumentException("Quantity cannot be null");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than 0");
    }
  }

  /**
   * Creates a Quantity from an integer value.
   *
   * @param value the quantity value
   * @return a new Quantity instance
   */
  public static RQuantity of(int value) {
    return new RQuantity(value);
  }
}
