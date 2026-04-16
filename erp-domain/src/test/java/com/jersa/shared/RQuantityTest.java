package com.jersa.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RQuantity Domain Test")
class RQuantityTest {

  final String msgNullEx = "Quantity cannot be null";
  final String msgInvalidEx = "Quantity must be greater than 0";

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Quantity Is Null")
  void shouldThrowIllegalArgumentExceptionWhenQuantityIsNull() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> new RQuantity(null));
    assertEquals(msgNullEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Quantity Is Less Than Or Equal To Zero")
  void shouldThrowIllegalArgumentExceptionWhenQuantityIsLessThanOrEqualToZero() {
    var exceptionForZero = assertThrows(IllegalArgumentException.class,
        () -> new RQuantity(0));
    assertEquals(msgInvalidEx, exceptionForZero.getMessage());

    var exceptionForNegative = assertThrows(IllegalArgumentException.class,
        () -> new RQuantity(-5));
    assertEquals(msgInvalidEx, exceptionForNegative.getMessage());
  }

  @Test
  @DisplayName("Should Create RQuantity Successfully With Positive Integer")
  void shouldCreateRQuantitySuccessfullyWithPositiveInteger() {
    Integer[] validQuantities = { 1, 10, 100, 999, Integer.MAX_VALUE };

    for (Integer validQuantity : validQuantities) {
      RQuantity quantity = new RQuantity(validQuantity);
      assertNotNull(quantity);
      assertEquals(validQuantity, quantity.value());
    }
  }

  @Test
  @DisplayName("Should Create RQuantity Using Of Factory Method")
  void shouldCreateRQuantityUsingOfFactoryMethod() {
    int quantityValue = 42;
    RQuantity quantity = RQuantity.of(quantityValue);

    assertNotNull(quantity);
    assertEquals(quantityValue, quantity.value());
  }

  @Test
  @DisplayName("Should Return Correct String Representation")
  void shouldReturnCorrectStringRepresentation() {
    int quantityValue = 25;
    RQuantity quantity = new RQuantity(quantityValue);

    assertEquals(quantityValue, quantity.value());
  }

  @Test
  @DisplayName("Should Consider Two Quantities Equal When They Have Same Value")
  void shouldConsiderTwoQuantitiesEqualWhenTheyHaveSameValue() {
    RQuantity quantity1 = new RQuantity(10);
    RQuantity quantity2 = new RQuantity(10);

    assertEquals(quantity1, quantity2);
    assertEquals(quantity1.hashCode(), quantity2.hashCode());
  }

  @Test
  @DisplayName("Should Consider Two Quantities Different When They Have Different Values")
  void shouldConsiderTwoQuantitiesDifferentWhenTheyHaveDifferentValues() {
    RQuantity quantity1 = new RQuantity(5);
    RQuantity quantity2 = new RQuantity(10);

    assertNotEquals(quantity1, quantity2);
  }

  @Test
  @DisplayName("Should Be Equal To Itself")
  void shouldBeEqualItself() {
    RQuantity quantity = new RQuantity(15);

    assertEquals(quantity, quantity);
  }

  @Test
  @DisplayName("Should Not Be Equal To Null Or Different Class")
  void shouldNotBeEqualNullOrDifferentClass() {
    RQuantity quantity = new RQuantity(20);

    assertNotEquals(null, quantity);
    assertNotEquals(20, quantity);
  }
}
