package com.jersa.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Domain Test")
class StockTest {
  @Test
  @DisplayName("Should Throw IllegalArgumentException When RStock Is Null")
  void shouldThrowIllegalArgumentExceptionWhenRStockIsNull() {
    final String msgEx = "Stock cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RStock(null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RStock Is Negative")
  void shouldThrowIllegalArgumentExceptionWhenRStockIsNegative() {
    final String msgEx = "Stock cannot be negative";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RStock(-1));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Create RStock With Valid Positive Value")
  void shouldCreateRStockWithValidPositiveValue() {
    RStock stock = new RStock(100);

    assertEquals(100, stock.value());
  }

  @Test
  @DisplayName("Should Create RStock Using of Method")
  void shouldCreateRStockUsingOfMethod() {
    RStock stock = RStock.of(50);

    assertEquals(50, stock.value());
  }

  @Test
  @DisplayName("Should Create RStock With Zero Value")
  void shouldCreateRStockWithZeroValue() {
    RStock stock = new RStock(0);

    assertEquals(0, stock.value());
  }

  @Test
  @DisplayName("Should Create Zero RStock Using zero Method")
  void shouldCreateZeroRStockUsingZeroMethod() {
    RStock stock = RStock.zero();

    assertEquals(0, stock.value());
  }

  @Test
  @DisplayName("Should Increment RStock By Valid Quantity")
  void shouldIncrementRStockByValidQuantity() {
    RStock stock = RStock.of(10);

    RStock newRStock = stock.increment(5);

    assertEquals(15, newRStock.value());
    assertEquals(10, stock.value()); // Original should be unchanged (immutable)
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Increment Quantity Is Negative")
  void shouldThrowIllegalArgumentExceptionWhenIncrementQuantityIsNegative() {
    RStock stock = RStock.of(10);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> stock.increment(-5));

    assertEquals("Increment quantity cannot be negative", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Allow Increment By Zero")
  void shouldAllowIncrementByZero() {
    RStock stock = RStock.of(10);

    RStock newRStock = stock.increment(0);

    assertEquals(10, newRStock.value());
  }

  @Test
  @DisplayName("Should Decrement RStock By Valid Quantity")
  void shouldDecrementRStockByValidQuantity() {
    RStock stock = RStock.of(10);

    RStock newRStock = stock.decrement(5);

    assertEquals(5, newRStock.value());
    assertEquals(10, stock.value()); // Original should be unchanged (immutable)
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Decrement Quantity Is Negative")
  void shouldThrowIllegalArgumentExceptionWhenDecrementQuantityIsNegative() {
    RStock stock = RStock.of(10);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> stock.decrement(-5));

    assertEquals("Decrement quantity cannot be negative", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Decrement Result Would Be Negative")
  void shouldThrowIllegalArgumentExceptionWhenDecrementResultWouldBeNegative() {
    RStock stock = RStock.of(10);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> stock.decrement(15));

    assertTrue(targetEx.getMessage().contains("Cannot decrement stock below zero"));
    assertTrue(targetEx.getMessage().contains("Current: 10"));
    assertTrue(targetEx.getMessage().contains("requested: 15"));
  }

  @Test
  @DisplayName("Should Allow Decrement To Zero")
  void shouldAllowDecrementToZero() {
    RStock stock = RStock.of(10);

    RStock newRStock = stock.decrement(10);

    assertEquals(0, newRStock.value());
  }

  @Test
  @DisplayName("Shouldn't Allow Decrement By Zero")
  void shouldntAllowDecrementByZero() {
    RStock stock = RStock.of(10);

    var targetEx = assertThrows(IllegalArgumentException.class,
        () -> stock.decrement(0));

    assertEquals("Decrement quantity cannot be negative", targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Return True When RStock Has Available Quantity")
  void shouldReturnTrueWhenRStockHasAvailableQuantity() {
    RStock stock = RStock.of(10);

    assertTrue(stock.hasAvailable(10));
    assertTrue(stock.hasAvailable(5));
    assertTrue(stock.hasAvailable(1));
  }

  @Test
  @DisplayName("Should Return False When RStock Does Not Have Available Quantity")
  void shouldReturnFalseWhenRStockDoesNotHaveAvailableQuantity() {
    RStock stock = RStock.of(10);

    assertFalse(stock.hasAvailable(11));
    assertFalse(stock.hasAvailable(100));
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By Value")
  void shouldSupportEqualsAndHashCodeByValue() {
    RStock stock1 = new RStock(10);
    RStock stock2 = new RStock(10);

    assertEquals(stock1, stock2);
    assertEquals(stock1.hashCode(), stock2.hashCode());
  }

  @Test
  @DisplayName("Should Not Be Equal When Values Differ")
  void shouldNotBeEqualWhenValuesDiffer() {
    RStock stock1 = new RStock(10);
    RStock stock2 = new RStock(20);

    assertNotEquals(stock1, stock2);
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    RStock stock = new RStock(10);

    assertNotNull(stock.toString());
    assertFalse(stock.toString().isEmpty());
    assertTrue(stock.toString().contains("10"));
  }
}
