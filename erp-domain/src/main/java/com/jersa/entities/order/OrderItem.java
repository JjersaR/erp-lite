package com.jersa.entities.order;

import com.jersa.common.Entity;
import com.jersa.entities.product.ProductRoot;
import com.jersa.entities.product.RProductId;
import com.jersa.shared.RMoney;
import com.jersa.shared.RQuantity;

import lombok.Getter;

@Getter
public class OrderItem extends Entity<ROrderItemId> {
  private RProductId productReference;
  private String productName;
  private RQuantity quantity;
  private RMoney unitPrice;
  private RMoney subtotal;

  protected OrderItem() {
    super(null);
  }

  /**
   * Creates an OrderItem from a Product and quantity.
   * This is a snapshot: product name and price are frozen at order creation.
   *
   * @param productRoot  the product to order
   * @param quantity the quantity to order
   * @return a new OrderItem instance
   * @throws IllegalArgumentException if product is inactive or has insufficient
   *                                  stock
   */
  public static OrderItem from(ProductRoot productRoot, RQuantity quantity) {
    if (productRoot == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (quantity == null) {
      throw new IllegalArgumentException("Quantity cannot be null");
    }
    if (!productRoot.isActive()) {
      throw new IllegalArgumentException("Cannot create order item for inactive product: " + productRoot.getSku().value());
    }
    if (!productRoot.hasAvailableStock(quantity.value())) {
      throw new IllegalArgumentException(
          "Insufficient stock for product " + productRoot.getSku().value() +
              ". Required: " + quantity.value() + ", Available: " + productRoot.getStock().value());
    }

    ROrderItemId orderItemId = ROrderItemId.generate();
    RMoney unitPrice = productRoot.getPrice();
    RMoney subtotal = unitPrice.multiply(quantity);

    return new OrderItem(
        orderItemId,
        productRoot.getId(),
        productRoot.getName().value(),
        quantity,
        unitPrice,
        subtotal);
  }

  private OrderItem(
      ROrderItemId id,
      RProductId productReference,
      String productName,
      RQuantity quantity,
      RMoney unitPrice,
      RMoney subtotal) {
    super(id);
    this.productReference = productReference;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.subtotal = subtotal;
  }

  /**
   * Calculates the subtotal (quantity * unitPrice).
   * This method exists for validation purposes.
   *
   * @return the calculated subtotal
   */
  public RMoney calculateSubtotal() {
    return this.unitPrice.multiply(this.quantity);
  }
}
