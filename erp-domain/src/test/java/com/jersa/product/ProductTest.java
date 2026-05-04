package com.jersa.product;

import com.jersa.entities.product.*;
import com.jersa.entities.product.events.RProductCreated;
import com.jersa.entities.product.events.RProductDeactivated;
import com.jersa.entities.product.events.RProductUpdated;
import com.jersa.entities.product.events.RStockChanged;
import com.jersa.shared.RMoney;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Test")
class ProductTest {
  private static final Currency USD = Currency.getInstance("USD");

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Price Is Null")
  void shouldThrowIllegalArgumentExceptionWhenPriceIsNull() {
    final String msgEx = "Price cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> ProductRoot.create(
            RSKU.of("LAPTOP-001"),
            RProductName.of("Laptop"),
            "Description",
            null,
            RStock.of(10),
            RCategoryReference.of("cat-electronics"),
            RProductImage.of("https://example.com/image.jpg"),
            "test-user"));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Price Is Zero Or Negative")
  void shouldThrowIllegalArgumentExceptionWhenPriceIsZeroOrNegative() {
    final String msgEx = "Price must be greater than 0";

    IllegalArgumentException targetExZero = assertThrows(IllegalArgumentException.class,
        () -> ProductRoot.create(
            RSKU.of("LAPTOP-001"),
            RProductName.of("Laptop"),
            "Description",
            RMoney.of(0.0, USD),
            RStock.of(10),
            RCategoryReference.of("cat-electronics"),
            RProductImage.of("https://example.com/image.jpg"),
            "test-user"));

    assertEquals(msgEx, targetExZero.getMessage());
  }

  @Test
  @DisplayName("Should Create Product With Valid Data And Register ProductCreated Event")
  void shouldCreateProductWithValidDataAndRegisterProductCreatedEvent() {
    RSKU sku = RSKU.of("LAPTOP-001");
    RProductName name = RProductName.of("Laptop Computer");
    String description = "High-performance laptop";
    RMoney price = RMoney.of(999.99, USD);
    RStock stock = RStock.of(100);
    RCategoryReference category = RCategoryReference.of("cat-electronics");
    RProductImage image = RProductImage.of("https://example.com/laptop.jpg");

    ProductRoot productRoot = ProductRoot.create(sku, name, description, price, stock, category, image, "test-user");

    assertNotNull(productRoot.getId());
    assertEquals(sku, productRoot.getSku());
    assertEquals(name, productRoot.getName());
    assertEquals(description, productRoot.getDescription());
    assertEquals(price, productRoot.getPrice());
    assertEquals(stock, productRoot.getStock());
    assertEquals(category, productRoot.getCategory());
    assertEquals(image, productRoot.getImage());
    assertTrue(productRoot.isActive());
    assertNotNull(productRoot.getAuditInfo());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    assertTrue(productRoot.getDomainEvents().get(0) instanceof RProductCreated);
  }

  @Test
  @DisplayName("Should Update Product Information And Register ProductUpdated Event")
  void shouldUpdateProductInformationAndRegisterProductUpdatedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.clearDomainEvents();

    RProductName newName = RProductName.of("Updated Laptop");
    String newDescription = "Updated description";
    RMoney newPrice = RMoney.of(1099.99, USD);
    RCategoryReference newCategory = RCategoryReference.of("cat-computers");
    RProductImage newImage = RProductImage.of("https://example.com/new-laptop.jpg");

    productRoot.update(newName, newDescription, newPrice, newCategory, newImage);

    assertEquals(newName, productRoot.getName());
    assertEquals(newDescription, productRoot.getDescription());
    assertEquals(newPrice, productRoot.getPrice());
    assertEquals(newCategory, productRoot.getCategory());
    assertEquals(newImage, productRoot.getImage());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    assertTrue(productRoot.getDomainEvents().get(0) instanceof RProductUpdated);
  }

  @Test
  @DisplayName("Should Increment RStock And Register RStockChanged Event")
  void shouldIncrementRStockAndRegisterRStockChangedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.clearDomainEvents();

    int initialRStock = productRoot.getStock().value();
    String reason = "Restocking from supplier";

    productRoot.incrementStock(50, reason);

    assertEquals(initialRStock + 50, productRoot.getStock().value());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    RStockChanged event = (RStockChanged) productRoot.getDomainEvents().get(0);
    assertEquals(initialRStock, event.oldStock());
    assertEquals(initialRStock + 50, event.newStock());
    assertEquals(reason, event.reason());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Increment Reason Is Null Or Blank")
  void shouldThrowIllegalArgumentExceptionWhenIncrementReasonIsNullOrBlank() {
    ProductRoot productRoot = createValidProduct();

    IllegalArgumentException targetExNull = assertThrows(IllegalArgumentException.class,
        () -> productRoot.incrementStock(10, null));

    assertEquals("Reason for stock increment cannot be null or blank", targetExNull.getMessage());

    IllegalArgumentException targetExBlank = assertThrows(IllegalArgumentException.class,
        () -> productRoot.incrementStock(10, "   "));

    assertEquals("Reason for stock increment cannot be null or blank", targetExBlank.getMessage());
  }

  @Test
  @DisplayName("Should Decrement RStock And Register RStockChanged Event")
  void shouldDecrementRStockAndRegisterRStockChangedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.clearDomainEvents();

    int initialRStock = productRoot.getStock().value();
    String reason = "Sold items";

    productRoot.decrementStock(20, reason);

    assertEquals(initialRStock - 20, productRoot.getStock().value());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    RStockChanged event = (RStockChanged) productRoot.getDomainEvents().get(0);
    assertEquals(initialRStock, event.oldStock());
    assertEquals(initialRStock - 20, event.newStock());
    assertEquals(reason, event.reason());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Decrement Reason Is Null Or Blank")
  void shouldThrowIllegalArgumentExceptionWhenDecrementReasonIsNullOrBlank() {
    ProductRoot productRoot = createValidProduct();

    IllegalArgumentException targetExNull = assertThrows(IllegalArgumentException.class,
        () -> productRoot.decrementStock(10, null));

    assertEquals("Reason for stock decrement cannot be null or blank", targetExNull.getMessage());

    IllegalArgumentException targetExBlank = assertThrows(IllegalArgumentException.class,
        () -> productRoot.decrementStock(10, ""));

    assertEquals("Reason for stock decrement cannot be null or blank", targetExBlank.getMessage());
  }

  @Test
  @DisplayName("Should Change Price And Register ProductUpdated Event")
  void shouldChangePriceAndRegisterProductUpdatedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.clearDomainEvents();

    RMoney newPrice = RMoney.of(1199.99, USD);

    productRoot.changePrice(newPrice);

    assertEquals(newPrice, productRoot.getPrice());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    assertTrue(productRoot.getDomainEvents().get(0) instanceof RProductUpdated);
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Changing To Null Or Invalid Price")
  void shouldThrowIllegalArgumentExceptionWhenChangingToNullOrInvalidPrice() {
    ProductRoot productRoot = createValidProduct();

    IllegalArgumentException targetExNull = assertThrows(IllegalArgumentException.class,
        () -> productRoot.changePrice(null));

    assertEquals("Price cannot be null", targetExNull.getMessage());

    IllegalArgumentException targetExZero = assertThrows(IllegalArgumentException.class,
        () -> productRoot.changePrice(RMoney.of(0.0, USD)));

    assertEquals("Price must be greater than 0", targetExZero.getMessage());
  }

  @Test
  @DisplayName("Should Deactivate Product And Register ProductDeactivated Event")
  void shouldDeactivateProductAndRegisterProductDeactivatedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.clearDomainEvents();

    assertTrue(productRoot.isActive());

    productRoot.deactivate();

    assertFalse(productRoot.isActive());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    assertTrue(productRoot.getDomainEvents().get(0) instanceof RProductDeactivated);
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Deactivating Already Deactivated Product")
  void shouldThrowIllegalStateExceptionWhenDeactivatingAlreadyDeactivatedProduct() {
    ProductRoot productRoot = createValidProduct();
    productRoot.deactivate();

    IllegalStateException targetEx = assertThrows(IllegalStateException.class,
        () -> productRoot.deactivate());

    assertEquals("Product is already deactivated", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Activate Product And Register ProductUpdated Event")
  void shouldActivateProductAndRegisterProductUpdatedEvent() {
    ProductRoot productRoot = createValidProduct();
    productRoot.deactivate();
    productRoot.clearDomainEvents();

    assertFalse(productRoot.isActive());

    productRoot.activate();

    assertTrue(productRoot.isActive());

    // Verify event registration
    assertEquals(1, productRoot.getDomainEvents().size());
    assertTrue(productRoot.getDomainEvents().get(0) instanceof RProductUpdated);
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Activating Already Active Product")
  void shouldThrowIllegalStateExceptionWhenActivatingAlreadyActiveProduct() {
    ProductRoot productRoot = createValidProduct();

    IllegalStateException targetEx = assertThrows(IllegalStateException.class,
        () -> productRoot.activate());

    assertEquals("Product is already active", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Return True When Product Has Available RStock And Is Active")
  void shouldReturnTrueWhenProductHasAvailableRStockAndIsActive() {
    ProductRoot productRoot = createProductWithRStock(100);

    assertTrue(productRoot.hasAvailableStock(50));
    assertTrue(productRoot.hasAvailableStock(100));
    assertTrue(productRoot.hasAvailableStock(1));
  }

  @Test
  @DisplayName("Should Return False When Product Does Not Have Available RStock")
  void shouldReturnFalseWhenProductDoesNotHaveAvailableRStock() {
    ProductRoot productRoot = createProductWithRStock(50);

    assertFalse(productRoot.hasAvailableStock(51));
    assertFalse(productRoot.hasAvailableStock(100));
  }

  @Test
  @DisplayName("Should Return False When Product Is Inactive Even With RStock")
  void shouldReturnFalseWhenProductIsInactiveEvenWithRStock() {
    ProductRoot productRoot = createProductWithRStock(100);
    productRoot.deactivate();

    assertFalse(productRoot.hasAvailableStock(10));
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By ID")
  void shouldSupportEqualsAndHashCodeByID() {
    ProductRoot productRoot1 = createValidProduct();
    ProductRoot productRoot2 = createValidProduct();

    // Different products should not be equal
    assertNotEquals(productRoot1, productRoot2);
    assertNotEquals(productRoot1.getId(), productRoot2.getId());
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    ProductRoot productRoot = createValidProduct();

    assertNotNull(productRoot.toString());
    assertFalse(productRoot.toString().isEmpty());
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

  private ProductRoot createProductWithRStock(int stockAmount) {
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
