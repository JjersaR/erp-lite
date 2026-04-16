package com.jersa.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jersa.common.AggregateRoot;
import com.jersa.order.events.ROrderCancelled;
import com.jersa.order.events.ROrderConfirmed;
import com.jersa.order.events.ROrderCreated;
import com.jersa.order.events.ROrderDelivered;
import com.jersa.order.events.ROrderShipped;
import com.jersa.shared.RAuditInfo;
import com.jersa.shared.RMoney;

import lombok.Getter;

@Getter
public class Order extends AggregateRoot<ROrderId> {
  private ROrderNumber orderNumber;
  private RCustomer customer;
  private ROrderStatus status;
  private List<OrderItem> items;
  private RMoney totalAmount;
  private RAuditInfo auditInfo;

  protected Order() {
    super(null);
  }

  private Order(
      ROrderId id,
      ROrderNumber orderNumber,
      RCustomer customer,
      ROrderStatus status,
      List<OrderItem> items,
      RMoney totalAmount,
      RAuditInfo auditInfo) {
    super(id);
    this.orderNumber = orderNumber;
    this.customer = customer;
    this.status = status;
    this.items = new ArrayList<>(items);
    this.totalAmount = totalAmount;
    this.auditInfo = auditInfo;
  }

  /**
   * Creates a new Order.
   *
   * @param orderNumber the unique order number
   * @param customer    the customer information
   * @param items       the list of order items (must not be empty)
   * @param createdBy   the user who created the order
   * @return a new Order instance in PENDING status
   */
  public static Order create(
      ROrderNumber orderNumber,
      RCustomer customer,
      List<OrderItem> items,
      String createdBy) {
    if (orderNumber == null) {
      throw new IllegalArgumentException("Order number cannot be null");
    }
    if (customer == null) {
      throw new IllegalArgumentException("Customer cannot be null");
    }
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Order must have at least one item");
    }

    validateItems(items);

    ROrderId orderId = ROrderId.generate();
    ROrderStatus status = ROrderStatus.pending();
    RMoney totalAmount = calculateTotal(items);
    Instant now = Instant.now();
    RAuditInfo auditInfo = RAuditInfo.create(createdBy, now);

    Order order = new Order(
        orderId,
        orderNumber,
        customer,
        status,
        items,
        totalAmount,
        auditInfo);

    order.registerEvent(new ROrderCreated(
        orderId,
        customer.customerId(),
        customer.customerName(),
        totalAmount,
        now));

    return order;
  }

  /**
   * Confirms the order (PENDING -> CONFIRMED).
   * This transition triggers stock decrement.
   */
  public void confirm() {
    ROrderStatus nextStatus = ROrderStatus.confirmed();
    validateTransition(nextStatus);

    this.status = nextStatus;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new ROrderConfirmed(this.id, Instant.now()));
  }

  /**
   * Ships the order (CONFIRMED -> SHIPPED).
   */
  public void ship() {
    ROrderStatus nextStatus = ROrderStatus.shipped();
    validateTransition(nextStatus);

    this.status = nextStatus;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new ROrderShipped(this.id, Instant.now()));
  }

  /**
   * Delivers the order (SHIPPED -> DELIVERED).
   * DELIVERED is a final state.
   */
  public void deliver() {
    ROrderStatus nextStatus = ROrderStatus.delivered();
    validateTransition(nextStatus);

    this.status = nextStatus;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new ROrderDelivered(this.id, Instant.now()));
  }

  /**
   * Cancels the order.
   * If order was CONFIRMED, stock must be released.
   *
   * @param reason the cancellation reason
   */
  public void cancel(String reason) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Cancellation reason cannot be null or blank");
    }

    ROrderStatus nextStatus = ROrderStatus.cancelled();
    validateTransition(nextStatus);

    this.status = nextStatus;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new ROrderCancelled(this.id, reason, Instant.now()));
  }

  /**
   * Adds an item to the order.
   * Only allowed in PENDING status.
   *
   * @param item the item to add
   */
  public void addItem(OrderItem item) {
    if (!this.status.isPending()) {
      throw new IllegalStateException("Cannot add items to order in status: " + this.status.value());
    }
    if (item == null) {
      throw new IllegalArgumentException("Order item cannot be null");
    }

    this.items.add(item);
    this.totalAmount = calculateTotal(this.items);
    this.auditInfo = this.auditInfo.updateTimestamp();
  }

  /**
   * Removes an item from the order.
   * Only allowed in PENDING status.
   *
   * @param item the item to remove
   */
  public void removeItem(OrderItem item) {
    if (!this.status.isPending()) {
      throw new IllegalStateException("Cannot remove items from order in status: " + this.status.value());
    }
    if (item == null) {
      throw new IllegalArgumentException("Order item cannot be null");
    }
    if (!this.items.remove(item)) {
      throw new IllegalArgumentException("Item not found in order");
    }
    if (this.items.isEmpty()) {
      throw new IllegalStateException("Order must have at least one item");
    }

    this.totalAmount = calculateTotal(this.items);
    this.auditInfo = this.auditInfo.updateTimestamp();
  }

  /**
   * Returns an unmodifiable copy of the order items.
   *
   * @return the list of order items
   */
  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  /**
   * Calculates the total amount by summing all item subtotals.
   *
   * @return the total amount
   */
  private static RMoney calculateTotal(List<OrderItem> items) {
    if (items.isEmpty()) {
      throw new IllegalArgumentException("Cannot calculate total for empty order");
    }

    RMoney total = items.get(0).getSubtotal();
    for (int i = 1; i < items.size(); i++) {
      total = total.add(items.get(i).getSubtotal());
    }

    return total;
  }

  /**
   * Validates that all items are valid.
   * - All items must have the same currency
   * - Each item's subtotal must equal quantity * unitPrice
   */
  private static void validateItems(List<OrderItem> items) {
    if (items.isEmpty()) {
      throw new IllegalArgumentException("Order items cannot be empty");
    }

    java.util.Currency firstCurrency = items.get(0).getUnitPrice().currency();

    items.forEach(item -> {
      if (!item.getUnitPrice().currency().equals(firstCurrency)) {
        throw new IllegalArgumentException(
            "All items must have the same currency. Expected: " + firstCurrency +
                ", found: " + item.getUnitPrice().currency());
      }

      // Validate subtotal calculation
      RMoney calculatedSubtotal = item.calculateSubtotal();
      if (!item.getSubtotal().equals(calculatedSubtotal)) {
        throw new IllegalArgumentException(
            "Item subtotal mismatch. Expected: " + calculatedSubtotal +
                ", found: " + item.getSubtotal());
      }

    });
  }

  /**
   * Validates that the current status can transition to the target status.
   *
   * @param nextStatus the target status
   * @throws IllegalStateException if the transition is invalid
   */
  private void validateTransition(ROrderStatus nextStatus) {
    if (!this.status.canTransitionTo(nextStatus)) {
      throw new IllegalStateException(
          "Invalid status transition from " + this.status.value() +
              " to " + nextStatus.value());
    }
  }
}
