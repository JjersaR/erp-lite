package com.jersa.order;

import com.jersa.entities.order.OrderItem;
import com.jersa.entities.product.*;
import com.jersa.shared.RMoney;
import com.jersa.shared.RQuantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Domain Test")
class OrderItemTest {
  private static final Currency USD = Currency.getInstance("USD");

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Product Is Null")
  void shouldThrowIllegalArgumentExceptionWhenProductIsNull() {
    final String msgEx = "Product cannot be null";
    RQuantity quantity = RQuantity.of(5);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> OrderItem.from(null, quantity));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RQuantity Is Null")
  void shouldThrowIllegalArgumentExceptionWhenRQuantityIsNull() {
    final String msgEx = "Quantity cannot be null";
    ProductRoot productRoot = createValidProduct();

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> OrderItem.from(productRoot, null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Product Is Inactive")
  void shouldThrowIllegalArgumentExceptionWhenProductIsInactive() {
    ProductRoot productRoot = createValidProduct();
    productRoot.deactivate();
    RQuantity quantity = RQuantity.of(5);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> OrderItem.from(productRoot, quantity));

    assertTrue(targetEx.getMessage().contains("Cannot create order item for inactive product"));
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Product Has Insufficient Stock")
  void shouldThrowIllegalArgumentExceptionWhenProductHasInsufficientStock() {
    ProductRoot productRoot = createProductWithStock(5);
    RQuantity quantity = RQuantity.of(10);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> OrderItem.from(productRoot, quantity));

    assertTrue(targetEx.getMessage().contains("Insufficient stock for product"));
    assertTrue(targetEx.getMessage().contains("Required: 10"));
    assertTrue(targetEx.getMessage().contains("Available: 5"));
  }

  @Test
  @DisplayName("Should Create OrderItem With Valid Product And RQuantity")
  void shouldCreateOrderItemWithValidProductAndRQuantity() {
    ProductRoot productRoot = createValidProduct();
    RQuantity quantity = RQuantity.of(5);

    OrderItem orderItem = OrderItem.from(productRoot, quantity);

    assertNotNull(orderItem.getId());
    assertEquals(productRoot.getId(), orderItem.getProductReference());
    assertEquals(productRoot.getName().value(), orderItem.getProductName());
    assertEquals(quantity, orderItem.getQuantity());
    assertEquals(productRoot.getPrice(), orderItem.getUnitPrice());
    assertEquals(productRoot.getPrice().multiply(quantity), orderItem.getSubtotal());
  }

  @Test
  @DisplayName("Should Capture Product Name And Price As Snapshot")
  void shouldCaptureProductNameAndPriceAsSnapshot() {
    ProductRoot productRoot = createValidProduct();
    RQuantity quantity = RQuantity.of(3);
    RMoney originalPrice = productRoot.getPrice();
    String originalName = productRoot.getName().value();

    OrderItem orderItem = OrderItem.from(productRoot, quantity);

    // Modify product after order item creation
    productRoot.changePrice(RMoney.of(999.99, USD));
    productRoot.update(
        RProductName.of("Modified Product Name"),
        "New Description",
        productRoot.getPrice(),
        productRoot.getCategory(),
        productRoot.getImage());

    // OrderItem should preserve original values
    assertEquals(originalName, orderItem.getProductName());
    assertEquals(originalPrice, orderItem.getUnitPrice());
    assertEquals(originalPrice.multiply(quantity), orderItem.getSubtotal());
  }

  @Test
  @DisplayName("Should Calculate Subtotal Correctly")
  void shouldCalculateSubtotalCorrectly() {
    ProductRoot productRoot = createValidProduct();
    RQuantity quantity = RQuantity.of(4);

    OrderItem orderItem = OrderItem.from(productRoot, quantity);

    RMoney expectedSubtotal = productRoot.getPrice().multiply(quantity);
    assertEquals(expectedSubtotal, orderItem.calculateSubtotal());
    assertEquals(expectedSubtotal, orderItem.getSubtotal());
  }

  @Test
  @DisplayName("Should Create OrderItem With Exact Stock Available")
  void shouldCreateOrderItemWithExactStockAvailable() {
    ProductRoot productRoot = createProductWithStock(10);
    RQuantity quantity = RQuantity.of(10);

    OrderItem orderItem = assertDoesNotThrow(() -> OrderItem.from(productRoot, quantity));

    assertEquals(quantity, orderItem.getQuantity());
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By ID")
  void shouldSupportEqualsAndHashCodeByID() {
    ProductRoot productRoot = createValidProduct();
    RQuantity quantity = RQuantity.of(5);

    OrderItem orderItem1 = OrderItem.from(productRoot, quantity);
    OrderItem orderItem2 = OrderItem.from(productRoot, quantity);

    // Different instances with different IDs should not be equal
    assertNotEquals(orderItem1, orderItem2);
    assertNotEquals(orderItem1.getId(), orderItem2.getId());
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    ProductRoot productRoot = createValidProduct();
    RQuantity quantity = RQuantity.of(5);

    OrderItem orderItem = OrderItem.from(productRoot, quantity);

    assertNotNull(orderItem.toString());
    assertFalse(orderItem.toString().isEmpty());
  }

  private ProductRoot createValidProduct() {
    return ProductRoot.create(
        RSKU.of("LAPTOP-001"),
        RProductName.of("Laptop Computer"),
        "High-performance laptop",
        RMoney.of(999.99, USD),
        RStock.of(100),
        RCategoryReference.of("cat-electronics"),
        RProductImage.of("https://example.com/laptop.jpg"),
        "test-user");
  }

  private ProductRoot createProductWithStock(int stockAmount) {
    return ProductRoot.create(
        RSKU.of("MOUSE-001"),
        RProductName.of("Wireless Mouse"),
        "Ergonomic wireless mouse",
        RMoney.of(29.99, USD),
        RStock.of(stockAmount),
        RCategoryReference.of("cat-electronics"),
        RProductImage.of("https://example.com/mouse.jpg"),
        "test-user");
  }
}
