package com.jersa.shared;

public record RCustomerId(Long value) {
  public RCustomerId {
    if (value == null) {
      throw new IllegalArgumentException("Customer ID cannot be null");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("Customer ID must be greater than 0");
    }
  }

  /**
   * Creates a CustomerId from a Long value.
   *
   * @param value the customer ID value
   * @return a new CustomerId instance
   */
  public static RCustomerId of(Long value) {
    return new RCustomerId(value);
  }
}
