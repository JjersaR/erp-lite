package com.jersa.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record RMoney(BigDecimal amount, Currency currency) {
  public RMoney {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
    if (currency == null) {
      throw new IllegalArgumentException("Currency cannot be null");
    }
    // Scale to 2 decimal places for consistency
    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Creates a Money instance from BigDecimal and Currency.
   *
   * @param amount   the monetary amount
   * @param currency the currency
   * @return a new Money instance
   */
  public static RMoney of(BigDecimal amount, Currency currency) {
    return new RMoney(amount, currency);
  }

  /**
   * Creates a Money instance from double and Currency.
   *
   * @param amount   the monetary amount
   * @param currency the currency
   * @return a new Money instance
   */
  public static RMoney of(double amount, Currency currency) {
    return new RMoney(BigDecimal.valueOf(amount), currency);
  }

  /**
   * Adds another Money value to this one.
   * Both Money objects must have the same currency.
   *
   * @param other the Money to add
   * @return a new Money instance with the sum
   * @throws IllegalArgumentException if currencies don't match
   */
  public RMoney add(RMoney other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Cannot add money with different currencies: " + this.currency + " and " + other.currency);
    }
    return new RMoney(this.amount.add(other.amount), this.currency);
  }

  /**
   * Subtracts another Money value from this one.
   * Both Money objects must have the same currency.
   *
   * @param other the Money to subtract
   * @return a new Money instance with the difference
   * @throws IllegalArgumentException if currencies don't match or result is
   *                                  negative
   */
  public RMoney subtract(RMoney other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Cannot subtract money with different currencies: " + this.currency + " and " + other.currency);
    }
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Subtraction result cannot be negative");
    }
    return new RMoney(result, this.currency);
  }

  /**
   * Multiplies this Money by an integer multiplier.
   *
   * @param multiplier the multiplier
   * @return a new Money instance with the product
   */
  public RMoney multiply(int multiplier) {
    if (multiplier < 0) {
      throw new IllegalArgumentException("Multiplier cannot be negative");
    }
    return new RMoney(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
  }

  /**
   * Multiplies this Money by a Quantity.
   *
   * @param quantity the quantity to multiply by
   * @return a new Money instance with the product
   */
  public RMoney multiply(RQuantity quantity) {
    return this.multiply(quantity.value());
  }

  @Override
  public String toString() {
    return amount + " " + currency.getCurrencyCode();
  }
}
