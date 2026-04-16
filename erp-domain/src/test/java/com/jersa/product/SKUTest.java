package com.jersa.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RSKU Domain Test")
class SKUTest {
  @Test
  @DisplayName("Should Throw IllegalArgumentException When RSKU Is Null")
  void shouldThrowIllegalArgumentExceptionWhenRSKUIsNull() {
    final String msgEx = "SKU cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RSKU(null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RSKU Format Is Invalid")
  void shouldThrowIllegalArgumentExceptionWhenRSKUFormatIsInvalid() {
    String[] invalidRSKUs = {
        "laptop-001", // lowercase letters
        "LAPTOP001", // missing hyphen
        "LAPTOP-01", // only 2 digits
        "LAPTOP-0001", // 4 digits
        "123-001", // starts with numbers
        "LAP TOP-001", // contains space
        "LAPTOP-ABC", // letters instead of numbers
        "", // empty
        "   ", // blank
        "-001" // missing prefix
    };

    for (String invalidRSKU : invalidRSKUs) {
      var targetEx = assertThrows(IllegalArgumentException.class,
          () -> new RSKU(invalidRSKU),
          "Should throw exception for: " + invalidRSKU);

      if (invalidRSKU == null) {
        assertTrue(targetEx.getMessage().contains("SKU cannot be null"));
      } else if (invalidRSKU.isBlank()) {
        assertTrue(targetEx.getMessage().contains("SKU cannot be blank"));
      } else {
        assertTrue(targetEx.getMessage().contains("Invalid SKU format"));
      }
    }
  }

  @Test
  @DisplayName("Should Create RSKU With Valid Format")
  void shouldCreateRSKUWithValidFormat() {
    String validRSKU = "LAPTOP-001";

    RSKU sku = new RSKU(validRSKU);

    assertEquals(validRSKU, sku.value());
  }

  @Test
  @DisplayName("Should Create RSKU Using of Method")
  void shouldCreateRSKUUsingOfMethod() {
    String validRSKU = "MOUSE-042";

    RSKU sku = RSKU.of(validRSKU);

    assertEquals(validRSKU, sku.value());
  }

  @Test
  @DisplayName("Should Accept Valid RSKU Formats")
  void shouldAcceptValidRSKUFormats() {
    String[] validRSKUs = {
        "LAPTOP-001",
        "MOUSE-042",
        "KEYBOARD-999",
        "AB-123",
        "A-001",
        "MONITOR-000",
        "HEADPHONES-555"
    };

    for (String validRSKU : validRSKUs) {
      RSKU sku = assertDoesNotThrow(() -> new RSKU(validRSKU),
          "Should not throw exception for: " + validRSKU);

      assertEquals(validRSKU, sku.value());
    }
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By Value")
  void shouldSupportEqualsAndHashCodeByValue() {
    RSKU sku1 = new RSKU("LAPTOP-001");
    RSKU sku2 = new RSKU("LAPTOP-001");

    assertEquals(sku1, sku2);
    assertEquals(sku1.hashCode(), sku2.hashCode());
  }

  @Test
  @DisplayName("Should Not Be Equal When Values Differ")
  void shouldNotBeEqualWhenValuesDiffer() {
    RSKU sku1 = new RSKU("LAPTOP-001");
    RSKU sku2 = new RSKU("LAPTOP-002");

    assertNotEquals(sku1, sku2);
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    RSKU sku = new RSKU("LAPTOP-001");

    assertNotNull(sku.toString());
    assertFalse(sku.toString().isEmpty());
    assertTrue(sku.toString().contains("LAPTOP-001"));
  }
}
