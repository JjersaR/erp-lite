package com.jersa.order;

import com.jersa.shared.RCustomerId;

public record RCustomer(
    RCustomerId customerId,
    String customerName) {
  public RCustomer {
    if (customerId == null) {
      throw new IllegalArgumentException("Customer ID cannot be null");
    }
    if (customerName == null || customerName.isBlank()) {
      throw new IllegalArgumentException("Customer name cannot be null or blank");
    }
  }

  /**
   * Creates a Customer from CustomerId and name.
   *
   * @param customerId   the customer identifier
   * @param customerName the customer name
   * @return a new Customer instance
   */
  public static RCustomer of(RCustomerId customerId, String customerName) {
    return new RCustomer(customerId, customerName);
  }
}
