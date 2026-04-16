package com.jersa.product;

import java.math.BigDecimal;
import java.time.Instant;

import com.jersa.common.AggregateRoot;
import com.jersa.product.events.RProductCreated;
import com.jersa.product.events.RProductDeactivated;
import com.jersa.product.events.RProductUpdated;
import com.jersa.product.events.RStockChanged;
import com.jersa.shared.RAuditInfo;
import com.jersa.shared.RMoney;

import lombok.Getter;

@Getter
public class Product extends AggregateRoot<RProductId> {
  private RSKU sku;
  private RProductName name;
  private String description;
  private RMoney price;
  private RStock stock;
  private RCategoryReference category;
  private RProductImage image;
  private boolean active;
  private RAuditInfo auditInfo;

  protected Product() {
    super(null);
  }

  private Product(
      RProductId id,
      RSKU sku,
      RProductName name,
      String description,
      RMoney price,
      RStock stock,
      RCategoryReference category,
      RProductImage image,
      boolean active,
      RAuditInfo auditInfo) {
    super(id);
    this.sku = sku;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.category = category;
    this.image = image;
    this.active = active;
    this.auditInfo = auditInfo;
  }

  /**
   * Creates a new Product.
   *
   * @param sku         the product SKU (unique and immutable)
   * @param name        the product name
   * @param description the product description (optional)
   * @param price       the product price (must be > 0)
   * @param stock       the initial stock
   * @param category    the category reference
   * @param image       the product image (optional)
   * @param createdBy   the user who created the product
   * @return a new Product instance
   */
  public static Product create(
      RSKU sku,
      RProductName name,
      String description,
      RMoney price,
      RStock stock,
      RCategoryReference category,
      RProductImage image,
      String createdBy) {
    validatePrice(price);

    RProductId productId = RProductId.generate();
    Instant now = Instant.now();
    RAuditInfo auditInfo = RAuditInfo.create(createdBy, now);

    Product product = new Product(
        productId,
        sku,
        name,
        description,
        price,
        stock,
        category,
        image,
        true,
        auditInfo);

    product.registerEvent(new RProductCreated(
        productId,
        sku,
        name,
        price,
        now));

    return product;
  }

  /**
   * Updates product information.
   *
   * @param name        the new product name
   * @param description the new description
   * @param price       the new price
   * @param category    the new category
   * @param image       the new image
   */
  public void update(
      RProductName name,
      String description,
      RMoney price,
      RCategoryReference category,
      RProductImage image) {
    validatePrice(price);

    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
    this.image = image;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RProductUpdated(this.id, Instant.now()));
  }

  /**
   * Increments the stock by the specified quantity.
   *
   * @param quantity the quantity to add
   * @param reason   the reason for the increment
   */
  public void incrementStock(int quantity, String reason) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Reason for stock increment cannot be null or blank");
    }

    Integer oldStock = this.stock.value();
    this.stock = this.stock.increment(quantity);
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RStockChanged(
        this.id,
        oldStock,
        this.stock.value(),
        reason,
        Instant.now()));
  }

  /**
   * Decrements the stock by the specified quantity.
   *
   * @param quantity the quantity to subtract
   * @param reason   the reason for the decrement
   * @throws IllegalArgumentException if insufficient stock
   */
  public void decrementStock(int quantity, String reason) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Reason for stock decrement cannot be null or blank");
    }

    Integer oldStock = this.stock.value();
    this.stock = this.stock.decrement(quantity);
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RStockChanged(
        this.id,
        oldStock,
        this.stock.value(),
        reason,
        Instant.now()));
  }

  /**
   * Changes the product price.
   *
   * @param newPrice the new price
   */
  public void changePrice(RMoney newPrice) {
    validatePrice(newPrice);

    this.price = newPrice;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RProductUpdated(this.id, Instant.now()));
  }

  /**
   * Deactivates the product.
   * Deactivated products cannot be ordered.
   */
  public void deactivate() {
    if (!this.active) {
      throw new IllegalStateException("Product is already deactivated");
    }

    this.active = false;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RProductDeactivated(this.id, Instant.now()));
  }

  /**
   * Activates the product.
   */
  public void activate() {
    if (this.active) {
      throw new IllegalStateException("Product is already active");
    }

    this.active = true;
    this.auditInfo = this.auditInfo.updateTimestamp();

    registerEvent(new RProductUpdated(this.id, Instant.now()));
  }

  /**
   * Checks if the product has the required quantity in stock.
   *
   * @param requiredQuantity the required quantity
   * @return true if stock is sufficient
   */
  public boolean hasAvailableStock(int requiredQuantity) {
    return this.active && this.stock.hasAvailable(requiredQuantity);
  }

  private static void validatePrice(RMoney price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (price.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be greater than 0");
    }
  }
}
