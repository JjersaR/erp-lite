package com.jersa.order;

import java.util.Set;

public record ROrderStatus(String value) {
  public static final String PENDING = "PENDING";
  public static final String CONFIRMED = "CONFIRMED";
  public static final String SHIPPED = "SHIPPED";
  public static final String DELIVERED = "DELIVERED";
  public static final String CANCELLED = "CANCELLED";

  private static final Set<String> VALID_STATUSES = Set.of(
      PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED);

  private static final Set<String> FINAL_STATES = Set.of(DELIVERED, CANCELLED);

  public ROrderStatus {
    if (value == null) {
      throw new IllegalArgumentException("Order status cannot be null");
    }
    if (!VALID_STATUSES.contains(value)) {
      throw new IllegalArgumentException("Invalid order status: " + value + ". Valid values: " + VALID_STATUSES);
    }
  }

  /**
   * Creates an OrderStatus from a String value.
   *
   * @param value the status value
   * @return a new OrderStatus instance
   */
  public static ROrderStatus of(String value) {
    return new ROrderStatus(value);
  }

  public static ROrderStatus pending() {
    return new ROrderStatus(PENDING);
  }

  public static ROrderStatus confirmed() {
    return new ROrderStatus(CONFIRMED);
  }

  public static ROrderStatus shipped() {
    return new ROrderStatus(SHIPPED);
  }

  public static ROrderStatus delivered() {
    return new ROrderStatus(DELIVERED);
  }

  public static ROrderStatus cancelled() {
    return new ROrderStatus(CANCELLED);
  }

  /**
   * Validates if this status can transition to the next status.
   * Valid transitions:
   * - PENDING -> CONFIRMED | CANCELLED
   * - CONFIRMED -> SHIPPED | CANCELLED
   * - SHIPPED -> DELIVERED
   * - DELIVERED -> (no transitions, final state)
   * - CANCELLED -> (no transitions, final state)
   *
   * @param nextStatus the target status
   * @return true if the transition is valid
   */
  public boolean canTransitionTo(ROrderStatus nextStatus) {
    return switch (this.value) {
      case PENDING -> CONFIRMED.equals(nextStatus.value) || CANCELLED.equals(nextStatus.value);
      case CONFIRMED -> SHIPPED.equals(nextStatus.value) || CANCELLED.equals(nextStatus.value);
      case SHIPPED -> DELIVERED.equals(nextStatus.value);
      case DELIVERED, CANCELLED -> false; // Final states
      default -> false;
    };
  }

  public boolean isPending() {
    return PENDING.equals(this.value);
  }

  public boolean isConfirmed() {
    return CONFIRMED.equals(this.value);
  }

  public boolean isShipped() {
    return SHIPPED.equals(this.value);
  }

  public boolean isDelivered() {
    return DELIVERED.equals(this.value);
  }

  public boolean isCancelled() {
    return CANCELLED.equals(this.value);
  }

  public boolean isFinalState() {
    return FINAL_STATES.contains(this.value);
  }
}
