package com.jersa.order;

import com.jersa.order.events.*;
import com.jersa.product.*;
import com.jersa.shared.RCustomerId;
import com.jersa.shared.RMoney;
import com.jersa.shared.RQuantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Domain Test")
class OrderTest {
  private static final Currency USD = Currency.getInstance("USD");

  @Test
  @DisplayName("Should Throw IllegalArgumentException When ROrderNumber Is Null")
  void shouldThrowIllegalArgumentExceptionWhenROrderNumberIsNull() {
    final String msgEx = "Order number cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> Order.create(
            null,
            createRCustomer(),
            createOrderItems(),
            "test-user"));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RCustomer Is Null")
  void shouldThrowIllegalArgumentExceptionWhenRCustomerIsNull() {
    final String msgEx = "Customer cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> Order.create(
            ROrderNumber.of("ORD-2025-001"),
            null,
            createOrderItems(),
            "test-user"));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Items List Is Null Or Empty")
  void shouldThrowIllegalArgumentExceptionWhenItemsListIsNullOrEmpty() {
    final String msgEx = "Order must have at least one item";

    IllegalArgumentException targetExNull = assertThrows(IllegalArgumentException.class,
        () -> Order.create(
            ROrderNumber.of("ORD-2025-001"),
            createRCustomer(),
            null,
            "test-user"));

    assertEquals(msgEx, targetExNull.getMessage());

    IllegalArgumentException targetExEmpty = assertThrows(IllegalArgumentException.class,
        () -> Order.create(
            ROrderNumber.of("ORD-2025-001"),
            createRCustomer(),
            List.of(),
            "test-user"));

    assertEquals(msgEx, targetExEmpty.getMessage());
  }

  @Test
  @DisplayName("Should Create Order With Valid Data And Register OrderCreated Event")
  void shouldCreateOrderWithValidDataAndRegisterOrderCreatedEvent() {
    ROrderNumber orderNumber = ROrderNumber.of("ORD-2025-001");
    RCustomer customer = createRCustomer();
    List<OrderItem> items = createOrderItems();

    Order order = Order.create(orderNumber, customer, items, "test-user");

    assertNotNull(order.getId());
    assertEquals(orderNumber, order.getOrderNumber());
    assertEquals(customer, order.getCustomer());
    assertTrue(order.getStatus().isPending());
    assertEquals(items.size(), order.getItems().size());
    assertNotNull(order.getTotalAmount());
    assertNotNull(order.getAuditInfo());

    // Verify event registration
    assertEquals(1, order.getDomainEvents().size());
    assertTrue(order.getDomainEvents().get(0) instanceof ROrderCreated);
  }

  @Test
  @DisplayName("Should Calculate Total Amount Correctly")
  void shouldCalculateTotalAmountCorrectly() {
    List<OrderItem> items = createOrderItems();
    Order order = createValidOrder();

    RMoney expectedTotal = items.get(0).getSubtotal();
    for (int i = 1; i < items.size(); i++) {
      expectedTotal = expectedTotal.add(items.get(i).getSubtotal());
    }

    assertEquals(expectedTotal, order.getTotalAmount());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Items Have Different Currencies")
  void shouldThrowIllegalArgumentExceptionWhenItemsHaveDifferentCurrencies() {
    Product product1 = createProduct("LAPTOP-001", "Laptop", 999.99, USD);
    Product product2 = createProduct("MOUSE-001", "Mouse", 29.99, Currency.getInstance("EUR"));

    OrderItem item1 = OrderItem.from(product1, RQuantity.of(1));
    OrderItem item2 = OrderItem.from(product2, RQuantity.of(1));

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> Order.create(
            ROrderNumber.of("ORD-2025-001"),
            createRCustomer(),
            List.of(item1, item2),
            "test-user"));

    assertTrue(targetEx.getMessage().contains("All items must have the same currency"));
  }

  @Test
  @DisplayName("Should Confirm Order And Register OrderConfirmed Event")
  void shouldConfirmOrderAndRegisterOrderConfirmedEvent() {
    Order order = createValidOrder();
    order.clearDomainEvents();

    assertTrue(order.getStatus().isPending());

    order.confirm();

    assertTrue(order.getStatus().isConfirmed());

    // Verify event registration
    assertEquals(1, order.getDomainEvents().size());
    assertTrue(order.getDomainEvents().get(0) instanceof ROrderConfirmed);
  }

  @Test
  @DisplayName("Should Ship Order And Register OrderShipped Event")
  void shouldShipOrderAndRegisterOrderShippedEvent() {
    Order order = createValidOrder();
    order.confirm();
    order.clearDomainEvents();

    assertTrue(order.getStatus().isConfirmed());

    order.ship();

    assertTrue(order.getStatus().isShipped());

    // Verify event registration
    assertEquals(1, order.getDomainEvents().size());
    assertTrue(order.getDomainEvents().get(0) instanceof ROrderShipped);
  }

  @Test
  @DisplayName("Should Deliver Order And Register OrderDelivered Event")
  void shouldDeliverOrderAndRegisterOrderDeliveredEvent() {
    Order order = createValidOrder();
    order.confirm();
    order.ship();
    order.clearDomainEvents();

    assertTrue(order.getStatus().isShipped());

    order.deliver();

    assertTrue(order.getStatus().isDelivered());

    // Verify event registration
    assertEquals(1, order.getDomainEvents().size());
    assertTrue(order.getDomainEvents().get(0) instanceof ROrderDelivered);
  }

  @Test
  @DisplayName("Should Cancel Order And Register OrderCancelled Event")
  void shouldCancelOrderAndRegisterOrderCancelledEvent() {
    Order order = createValidOrder();
    order.clearDomainEvents();

    String reason = "RCustomer requested cancellation";

    order.cancel(reason);

    assertTrue(order.getStatus().isCancelled());

    // Verify event registration
    assertEquals(1, order.getDomainEvents().size());
    ROrderCancelled event = (ROrderCancelled) order.getDomainEvents().get(0);
    assertEquals(reason, event.reason());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Cancellation Reason Is Null Or Blank")
  void shouldThrowIllegalArgumentExceptionWhenCancellationReasonIsNullOrBlank() {
    Order order = createValidOrder();

    IllegalArgumentException targetExNull = assertThrows(IllegalArgumentException.class,
        () -> order.cancel(null));

    assertEquals("Cancellation reason cannot be null or blank", targetExNull.getMessage());

    IllegalArgumentException targetExBlank = assertThrows(IllegalArgumentException.class,
        () -> order.cancel("   "));

    assertEquals("Cancellation reason cannot be null or blank", targetExBlank.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Invalid Status Transition")
  void shouldThrowIllegalStateExceptionWhenInvalidStatusTransition() {
    Order order = createValidOrder();

    // Cannot ship a PENDING order
    IllegalStateException targetEx1 = assertThrows(IllegalStateException.class,
        () -> order.ship());

    assertTrue(targetEx1.getMessage().contains("Invalid status transition"));

    // Cannot deliver a PENDING order
    IllegalStateException targetEx2 = assertThrows(IllegalStateException.class,
        () -> order.deliver());

    assertTrue(targetEx2.getMessage().contains("Invalid status transition"));
  }

  @Test
  @DisplayName("Should Add Item To Order When Status Is PENDING")
  void shouldAddItemToOrderWhenStatusIsPENDING() {
    Order order = createValidOrder();
    int initialItemCount = order.getItems().size();
    RMoney initialTotal = order.getTotalAmount();

    Product newProduct = createProduct("KEYBOARD-001", "Keyboard", 79.99, USD);
    OrderItem newItem = OrderItem.from(newProduct, RQuantity.of(1));

    order.addItem(newItem);

    assertEquals(initialItemCount + 1, order.getItems().size());
    assertTrue(order.getTotalAmount().amount().compareTo(initialTotal.amount()) > 0);
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Adding Item To Non-PENDING Order")
  void shouldThrowIllegalStateExceptionWhenAddingItemToNonPENDINGOrder() {
    Order order = createValidOrder();
    order.confirm();

    Product newProduct = createProduct("KEYBOARD-001", "Keyboard", 79.99, USD);
    OrderItem newItem = OrderItem.from(newProduct, RQuantity.of(1));

    IllegalStateException targetEx = assertThrows(IllegalStateException.class,
        () -> order.addItem(newItem));

    assertTrue(targetEx.getMessage().contains("Cannot add items to order in status"));
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Adding Null Item")
  void shouldThrowIllegalArgumentExceptionWhenAddingNullItem() {
    Order order = createValidOrder();

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> order.addItem(null));

    assertEquals("Order item cannot be null", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Remove Item From Order When Status Is PENDING")
  void shouldRemoveItemFromOrderWhenStatusIsPENDING() {
    Order order = createValidOrder();
    OrderItem itemToRemove = order.getItems().get(0);
    int initialItemCount = order.getItems().size();
    RMoney initialTotal = order.getTotalAmount();

    order.removeItem(itemToRemove);

    assertEquals(initialItemCount - 1, order.getItems().size());
    assertTrue(order.getTotalAmount().amount().compareTo(initialTotal.amount()) < 0);
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Removing Item From Non-PENDING Order")
  void shouldThrowIllegalStateExceptionWhenRemovingItemFromNonPENDINGOrder() {
    Order order = createValidOrder();
    order.confirm();
    OrderItem itemToRemove = order.getItems().get(0);

    IllegalStateException targetEx = assertThrows(IllegalStateException.class,
        () -> order.removeItem(itemToRemove));

    assertTrue(targetEx.getMessage().contains("Cannot remove items from order in status"));
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Removing Null Item")
  void shouldThrowIllegalArgumentExceptionWhenRemovingNullItem() {
    Order order = createValidOrder();

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> order.removeItem(null));

    assertEquals("Order item cannot be null", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Removing Non-Existent Item")
  void shouldThrowIllegalArgumentExceptionWhenRemovingNonExistentItem() {
    Order order = createValidOrder();
    Product otherProduct = createProduct("OTHER-001", "Other", 50.0, USD);
    OrderItem nonExistentItem = OrderItem.from(otherProduct, RQuantity.of(1));

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> order.removeItem(nonExistentItem));

    assertEquals("Item not found in order", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalStateException When Removing Last Item")
  void shouldThrowIllegalStateExceptionWhenRemovingLastItem() {
    Product product = createProduct("LAPTOP-001", "Laptop", 999.99, USD);
    OrderItem item = OrderItem.from(product, RQuantity.of(1));
    Order order = Order.create(
        ROrderNumber.of("ORD-2025-001"),
        createRCustomer(),
        List.of(item),
        "test-user");

    IllegalStateException targetEx = assertThrows(IllegalStateException.class,
        () -> order.removeItem(item));

    assertEquals("Order must have at least one item", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Return Unmodifiable List Of Items")
  void shouldReturnUnmodifiableListOfItems() {
    Order order = createValidOrder();
    List<OrderItem> items = order.getItems();

    Product newProduct = createProduct("KEYBOARD-001", "Keyboard", 79.99, USD);
    OrderItem newItem = OrderItem.from(newProduct, RQuantity.of(1));

    assertThrows(UnsupportedOperationException.class,
        () -> items.add(newItem));
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By ID")
  void shouldSupportEqualsAndHashCodeByID() {
    Order order1 = createValidOrder();
    Order order2 = createValidOrder();

    // Different orders should not be equal
    assertNotEquals(order1, order2);
    assertNotEquals(order1.getId(), order2.getId());
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    Order order = createValidOrder();

    assertNotNull(order.toString());
    assertFalse(order.toString().isEmpty());
  }

  private Order createValidOrder() {
    return Order.create(
        ROrderNumber.of("ORD-2025-001"),
        createRCustomer(),
        createOrderItems(),
        "test-user");
  }

  private RCustomer createRCustomer() {
    return RCustomer.of(RCustomerId.of(1L), "John Doe");
  }

  private List<OrderItem> createOrderItems() {
    Product product1 = createProduct("LAPTOP-001", "Laptop Computer", 999.99, USD);
    Product product2 = createProduct("MOUSE-001", "Wireless Mouse", 29.99, USD);

    OrderItem item1 = OrderItem.from(product1, RQuantity.of(1));
    OrderItem item2 = OrderItem.from(product2, RQuantity.of(2));

    return List.of(item1, item2);
  }

  private Product createProduct(String skuValue, String name, double price, Currency currency) {
    return Product.create(
        RSKU.of(skuValue),
        RProductName.of(name),
        "Description for " + name,
        RMoney.of(price, currency),
        RStock.of(100),
        RCategoryReference.of("cat-electronics"),
        RProductImage.of("https://example.com/image.jpg"),
        "test-user");
  }
}
