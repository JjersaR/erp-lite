package com.jersa.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RProductName Domain Test")
class ProductNameTest {
  @Test
  @DisplayName("Should Throw IllegalArgumentException When RProductName Is Null")
  void shouldThrowIllegalArgumentExceptionWhenRProductNameIsNull() {
    final String msgEx = "Product name cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RProductName(null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RProductName Is Too Short")
  void shouldThrowIllegalArgumentExceptionWhenRProductNameIsTooShort() {
    String[] shortNames = { "", "A", "AB" };

    for (String shortName : shortNames) {
      IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
          () -> new RProductName(shortName),
          "Should throw exception for: '" + shortName + "'");

      assertTrue(targetEx.getMessage().contains("must have at least 3 characters"),
          "Exception message should contain 'must have at least 3 characters' for: " + shortName);
    }
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When RProductName Is Too Long")
  void shouldThrowIllegalArgumentExceptionWhenRProductNameIsTooLong() {
    String longName = "A".repeat(201);

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RProductName(longName));

    assertTrue(targetEx.getMessage().contains("cannot exceed 200 characters"));
  }

  @Test
  @DisplayName("Should Create RProductName With Valid Length")
  void shouldCreateRProductNameWithValidLength() {
    String validName = "Laptop Computer";

    RProductName productName = new RProductName(validName);

    assertEquals(validName, productName.value());
  }

  @Test
  @DisplayName("Should Create RProductName Using of Method")
  void shouldCreateRProductNameUsingOfMethod() {
    String validName = "Wireless Mouse";

    RProductName productName = RProductName.of(validName);

    assertEquals(validName, productName.value());
  }

  @Test
  @DisplayName("Should Accept RProductName With Minimum Length")
  void shouldAcceptRProductNameWithMinimumLength() {
    String minName = "ABC";

    RProductName productName = assertDoesNotThrow(() -> new RProductName(minName));

    assertEquals(minName, productName.value());
  }

  @Test
  @DisplayName("Should Accept RProductName With Maximum Length")
  void shouldAcceptRProductNameWithMaximumLength() {
    String maxName = "A".repeat(200);

    RProductName productName = assertDoesNotThrow(() -> new RProductName(maxName));

    assertEquals(maxName, productName.value());
  }

  @Test
  @DisplayName("Should Accept RProductName With Special Characters")
  void shouldAcceptRProductNameWithSpecialCharacters() {
    String[] validNames = {
        "Laptop & Desktop",
        "Mouse (Wireless)",
        "Keyboard - Mechanical",
        "Monitor 27\"",
        "Product: Test 123"
    };

    for (String validName : validNames) {
      RProductName productName = assertDoesNotThrow(() -> new RProductName(validName),
          "Should not throw exception for: " + validName);

      assertEquals(validName, productName.value());
    }
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By Value")
  void shouldSupportEqualsAndHashCodeByValue() {
    RProductName productName1 = new RProductName("Laptop Computer");
    RProductName productName2 = new RProductName("Laptop Computer");

    assertEquals(productName1, productName2);
    assertEquals(productName1.hashCode(), productName2.hashCode());
  }

  @Test
  @DisplayName("Should Not Be Equal When Values Differ")
  void shouldNotBeEqualWhenValuesDiffer() {
    RProductName productName1 = new RProductName("Laptop Computer");
    RProductName productName2 = new RProductName("Desktop Computer");

    assertNotEquals(productName1, productName2);
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    RProductName productName = new RProductName("Laptop Computer");

    assertNotNull(productName.toString());
    assertFalse(productName.toString().isEmpty());
    assertTrue(productName.toString().contains("Laptop Computer"));
  }
}
