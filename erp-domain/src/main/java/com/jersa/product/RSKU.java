package com.jersa.product;

import java.util.regex.Pattern;

public record RSKU(String value) {
  private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z]+-\\d{3}$");

  public RSKU {
    if (value == null) {
      throw new IllegalArgumentException("SKU cannot be null");
    }
    if (value.isBlank()) {
      throw new IllegalArgumentException("SKU cannot be blank");
    }
    if (!SKU_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid SKU format. Expected pattern: [A-Z]+-\\d{3}, got: " + value);
    }
  }

  /**
   * Creates a SKU from a String value.
   *
   * @param value the SKU value
   * @return a new SKU instance
   */
  public static RSKU of(String value) {
    return new RSKU(value);
  }
}
