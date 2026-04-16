package com.jersa.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RMoney Domain Test")
class RMoneyTest {

  private static final Currency USD = Currency.getInstance("USD");
  private static final Currency EUR = Currency.getInstance("EUR");
  private static final Currency MXN = Currency.getInstance("MXN");

  final String msgAmountNullEx = "Amount cannot be null";
  final String msgAmountNegativeEx = "Amount cannot be negative";
  final String msgQuantityNegativeEx = "Quantity must be greater than 0";
  final String msgCurrencyNullEx = "Currency cannot be null";
  final String msgDifferentCurrenciesEx = "Cannot add money with different currencies";
  final String msgSubtractDifferentCurrenciesEx = "Cannot subtract money with different currencies";
  final String msgSubtractNegativeResultEx = "Subtraction result cannot be negative";
  final String msgMultiplierNegativeEx = "Multiplier cannot be negative";

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Amount Is Null")
  void shouldThrowIllegalArgumentExceptionWhenAmountIsNull() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> new RMoney(null, USD));
    assertEquals(msgAmountNullEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Amount Is Negative")
  void shouldThrowIllegalArgumentExceptionWhenAmountIsNegative() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> new RMoney(BigDecimal.valueOf(-10.50), USD));
    assertEquals(msgAmountNegativeEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Currency Is Null")
  void shouldThrowIllegalArgumentExceptionWhenCurrencyIsNull() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> new RMoney(BigDecimal.valueOf(100.50), null));
    assertEquals(msgCurrencyNullEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Scale Amount To Two Decimal Places")
  void shouldScaleAmountToTwoDecimalPlaces() {
    RMoney money1 = new RMoney(BigDecimal.valueOf(10.555), USD);
    assertEquals(2, money1.amount().scale());
    assertEquals(10.56, money1.amount().doubleValue());

    RMoney money2 = new RMoney(BigDecimal.valueOf(10.554), USD);
    assertEquals(10.55, money2.amount().doubleValue());
  }

  @Test
  @DisplayName("Should Create RMoney Successfully With Valid Parameters")
  void shouldCreateRMoneySuccessfullyWithValidParameters() {
    BigDecimal amount = BigDecimal.valueOf(99.99);
    RMoney money = new RMoney(amount, USD);

    assertNotNull(money);
    assertEquals(amount.setScale(2, BigDecimal.ROUND_HALF_UP), money.amount());
    assertEquals(USD, money.currency());
  }

  @Test
  @DisplayName("Should Create RMoney Using Of Factory Method With BigDecimal")
  void shouldCreateRMoneyUsingOfFactoryMethodWithBigDecimal() {
    BigDecimal amount = BigDecimal.valueOf(49.99);
    RMoney money = RMoney.of(amount, USD);

    assertNotNull(money);
    assertEquals(amount.setScale(2, BigDecimal.ROUND_HALF_UP), money.amount());
    assertEquals(USD, money.currency());
  }

  @Test
  @DisplayName("Should Create RMoney Using Of Factory Method With Double")
  void shouldCreateRMoneyUsingOfFactoryMethodWithDouble() {
    double amount = 75.50;
    RMoney money = RMoney.of(amount, USD);

    assertNotNull(money);
    assertEquals(BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP), money.amount());
    assertEquals(USD, money.currency());
  }

  @Test
  @DisplayName("Should Add Two Money Objects With Same Currency")
  void shouldAddTwoMoneyObjectsWithSameCurrency() {
    RMoney money1 = RMoney.of(10.50, USD);
    RMoney money2 = RMoney.of(5.25, USD);
    RMoney result = money1.add(money2);

    assertEquals(15.75, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Adding Different Currencies")
  void shouldThrowIllegalArgumentExceptionWhenAddingDifferentCurrencies() {
    RMoney money1 = RMoney.of(10.50, USD);
    RMoney money2 = RMoney.of(5.25, EUR);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> money1.add(money2));
    assertTrue(exception.getMessage().contains(msgDifferentCurrenciesEx));
  }

  @Test
  @DisplayName("Should Subtract Two Money Objects With Same Currency")
  void shouldSubtractTwoMoneyObjectsWithSameCurrency() {
    RMoney money1 = RMoney.of(20.00, USD);
    RMoney money2 = RMoney.of(7.50, USD);
    RMoney result = money1.subtract(money2);

    assertEquals(12.50, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Should Subtract When Result Is Zero")
  void shouldSubtractWhenResultIsZero() {
    RMoney money1 = RMoney.of(10.00, USD);
    RMoney money2 = RMoney.of(10.00, USD);
    RMoney result = money1.subtract(money2);

    assertEquals(0.00, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Subtracting Different Currencies")
  void shouldThrowIllegalArgumentExceptionWhenSubtractingDifferentCurrencies() {
    RMoney money1 = RMoney.of(20.00, USD);
    RMoney money2 = RMoney.of(7.50, EUR);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> money1.subtract(money2));
    assertTrue(exception.getMessage().contains(msgSubtractDifferentCurrenciesEx));
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Subtraction Results In Negative")
  void shouldThrowIllegalArgumentExceptionWhenSubtractionResultsInNegative() {
    RMoney money1 = RMoney.of(10.00, USD);
    RMoney money2 = RMoney.of(15.00, USD);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> money1.subtract(money2));
    assertEquals(msgSubtractNegativeResultEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Multiply Money By Positive Integer")
  void shouldMultiplyMoneyByPositiveInteger() {
    RMoney money = RMoney.of(10.50, USD);
    RMoney result = money.multiply(3);

    assertEquals(31.50, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Should Multiply Money By Zero")
  void shouldMultiplyMoneyByZero() {
    RMoney money = RMoney.of(10.50, USD);
    RMoney result = money.multiply(0);

    assertEquals(0.00, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Multiplying By Negative Integer")
  void shouldThrowIllegalArgumentExceptionWhenMultiplyingByNegativeInteger() {
    RMoney money = RMoney.of(10.50, USD);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> money.multiply(-3));
    assertEquals(msgMultiplierNegativeEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Multiply Money By RQuantity")
  void shouldMultiplyMoneyByRQuantity() {
    RMoney money = RMoney.of(10.50, USD);
    RQuantity quantity = RQuantity.of(4);
    RMoney result = money.multiply(quantity);

    assertEquals(42.00, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }

  @Test
  @DisplayName("Shouldn't Create RQuantity With Zero Value")
  void shouldntCreateRQuantityWithZeroValue() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new RQuantity(0));

    assertEquals(msgQuantityNegativeEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Return Correct String Representation")
  void shouldReturnCorrectStringRepresentation() {
    RMoney money = RMoney.of(99.99, USD);
    String expected = "99.99 USD";

    assertEquals(expected, money.toString());
  }

  @Test
  @DisplayName("Should Consider Two Money Objects Equal When Amount And Currency Are Same")
  void shouldConsiderTwoMoneyObjectsEqualWhenAmountAndCurrencyAreSame() {
    RMoney money1 = RMoney.of(100.50, USD);
    RMoney money2 = RMoney.of(100.50, USD);

    assertEquals(money1, money2);
    assertEquals(money1.hashCode(), money2.hashCode());
  }

  @Test
  @DisplayName("Should Consider Two Money Objects Different When Amounts Differ")
  void shouldConsiderTwoMoneyObjectsDifferentWhenAmountsDiffer() {
    RMoney money1 = RMoney.of(100.50, USD);
    RMoney money2 = RMoney.of(200.75, USD);

    assertNotEquals(money1, money2);
  }

  @Test
  @DisplayName("Should Consider Two Money Objects Different When Currencies Differ")
  void shouldConsiderTwoMoneyObjectsDifferentWhenCurrenciesDiffer() {
    RMoney money1 = RMoney.of(100.50, USD);
    RMoney money2 = RMoney.of(100.50, EUR);

    assertNotEquals(money1, money2);
  }

  @Test
  @DisplayName("Should Be Equal To Itself")
  void shouldBeEqualItself() {
    RMoney money = RMoney.of(50.00, USD);

    assertEquals(money, money);
  }

  @Test
  @DisplayName("Should Not Be Equal To Null Or Different Class")
  void shouldNotBeEqualNullOrDifferentClass() {
    RMoney money = RMoney.of(50.00, USD);

    assertNotEquals(null, money);
    assertNotEquals("50.00 USD", money);
  }

  @Test
  @DisplayName("Should Handle Precision Correctly With Double Values")
  void shouldHandlePrecisionCorrectlyWithDoubleValues() {
    RMoney money = RMoney.of(10.999, USD);
    assertEquals(11.00, money.amount().doubleValue());

    RMoney money2 = RMoney.of(10.001, USD);
    assertEquals(10.00, money2.amount().doubleValue());
  }

  @Test
  @DisplayName("Should Work With Different Currencies")
  void shouldWorkWithDifferentCurrencies() {
    RMoney usdMoney = RMoney.of(100.00, USD);
    RMoney eurMoney = RMoney.of(85.50, EUR);
    RMoney mxnMoney = RMoney.of(2000.00, MXN);

    assertEquals(USD, usdMoney.currency());
    assertEquals(EUR, eurMoney.currency());
    assertEquals(MXN, mxnMoney.currency());
  }

  @Test
  @DisplayName("Should Chain Operations Correctly")
  void shouldChainOperationsCorrectly() {
    RMoney money = RMoney.of(10.00, USD);

    RMoney result = money
        .add(RMoney.of(5.00, USD))
        .multiply(2)
        .subtract(RMoney.of(5.00, USD));

    assertEquals(25.00, result.amount().doubleValue());
    assertEquals(USD, result.currency());
  }
}
