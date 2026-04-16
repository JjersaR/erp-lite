package com.jersa.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderStatus Domain Test")
class OrderStatusTest {
  @Test
  @DisplayName("Should Throw IllegalArgumentException When Status Is Null")
  void shouldThrowIllegalArgumentExceptionWhenStatusIsNull() {
    final String msgEx = "Order status cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new ROrderStatus(null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Status Is Invalid")
  void shouldThrowIllegalArgumentExceptionWhenStatusIsInvalid() {
    String[] invalidStatuses = { "INVALID", "PROCESSING", "COMPLETED", "", "pending", "confirmed" };

    for (String invalidStatus : invalidStatuses) {
      IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
          () -> new ROrderStatus(invalidStatus),
          "Should throw exception for: " + invalidStatus);

      assertTrue(targetEx.getMessage().contains("Invalid order status"),
          "Exception message should contain 'Invalid order status' for: " + invalidStatus);
    }
  }

  @Test
  @DisplayName("Should Create ROrderStatus With Valid Status Values")
  void shouldCreateROrderStatusWithValidStatusValues() {
    ROrderStatus pending = new ROrderStatus(ROrderStatus.PENDING);
    ROrderStatus confirmed = new ROrderStatus(ROrderStatus.CONFIRMED);
    ROrderStatus shipped = new ROrderStatus(ROrderStatus.SHIPPED);
    ROrderStatus delivered = new ROrderStatus(ROrderStatus.DELIVERED);
    ROrderStatus cancelled = new ROrderStatus(ROrderStatus.CANCELLED);

    assertEquals(ROrderStatus.PENDING, pending.value());
    assertEquals(ROrderStatus.CONFIRMED, confirmed.value());
    assertEquals(ROrderStatus.SHIPPED, shipped.value());
    assertEquals(ROrderStatus.DELIVERED, delivered.value());
    assertEquals(ROrderStatus.CANCELLED, cancelled.value());
  }

  @Test
  @DisplayName("Should Create ROrderStatus Using of Method")
  void shouldCreateROrderStatusUsingOfMethod() {
    ROrderStatus status = ROrderStatus.of(ROrderStatus.PENDING);

    assertEquals(ROrderStatus.PENDING, status.value());
  }

  @Test
  @DisplayName("Should Create ROrderStatus Using Factory Methods")
  void shouldCreateROrderStatusUsingFactoryMethods() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus confirmed = ROrderStatus.confirmed();
    ROrderStatus shipped = ROrderStatus.shipped();
    ROrderStatus delivered = ROrderStatus.delivered();
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertEquals(ROrderStatus.PENDING, pending.value());
    assertEquals(ROrderStatus.CONFIRMED, confirmed.value());
    assertEquals(ROrderStatus.SHIPPED, shipped.value());
    assertEquals(ROrderStatus.DELIVERED, delivered.value());
    assertEquals(ROrderStatus.CANCELLED, cancelled.value());
  }

  @Test
  @DisplayName("Should Allow Transition From PENDING To CONFIRMED")
  void shouldAllowTransitionFromPENDINGToCONFIRMED() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus confirmed = ROrderStatus.confirmed();

    assertTrue(pending.canTransitionTo(confirmed));
  }

  @Test
  @DisplayName("Should Allow Transition From PENDING To CANCELLED")
  void shouldAllowTransitionFromPENDINGToCANCELLED() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertTrue(pending.canTransitionTo(cancelled));
  }

  @Test
  @DisplayName("Should Allow Transition From CONFIRMED To SHIPPED")
  void shouldAllowTransitionFromCONFIRMEDToSHIPPED() {
    ROrderStatus confirmed = ROrderStatus.confirmed();
    ROrderStatus shipped = ROrderStatus.shipped();

    assertTrue(confirmed.canTransitionTo(shipped));
  }

  @Test
  @DisplayName("Should Allow Transition From CONFIRMED To CANCELLED")
  void shouldAllowTransitionFromCONFIRMEDToCANCELLED() {
    ROrderStatus confirmed = ROrderStatus.confirmed();
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertTrue(confirmed.canTransitionTo(cancelled));
  }

  @Test
  @DisplayName("Should Allow Transition From SHIPPED To DELIVERED")
  void shouldAllowTransitionFromSHIPPEDToDELIVERED() {
    ROrderStatus shipped = ROrderStatus.shipped();
    ROrderStatus delivered = ROrderStatus.delivered();

    assertTrue(shipped.canTransitionTo(delivered));
  }

  @Test
  @DisplayName("Should Not Allow Transition From PENDING To SHIPPED")
  void shouldNotAllowTransitionFromPENDINGToSHIPPED() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus shipped = ROrderStatus.shipped();

    assertFalse(pending.canTransitionTo(shipped));
  }

  @Test
  @DisplayName("Should Not Allow Transition From PENDING To DELIVERED")
  void shouldNotAllowTransitionFromPENDINGToDELIVERED() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus delivered = ROrderStatus.delivered();

    assertFalse(pending.canTransitionTo(delivered));
  }

  @Test
  @DisplayName("Should Not Allow Transition From CONFIRMED To DELIVERED")
  void shouldNotAllowTransitionFromCONFIRMEDToDELIVERED() {
    ROrderStatus confirmed = ROrderStatus.confirmed();
    ROrderStatus delivered = ROrderStatus.delivered();

    assertFalse(confirmed.canTransitionTo(delivered));
  }

  @Test
  @DisplayName("Should Not Allow Transition From SHIPPED To CANCELLED")
  void shouldNotAllowTransitionFromSHIPPEDToCANCELLED() {
    ROrderStatus shipped = ROrderStatus.shipped();
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertFalse(shipped.canTransitionTo(cancelled));
  }

  @Test
  @DisplayName("Should Not Allow Transitions From DELIVERED")
  void shouldNotAllowTransitionsFromDELIVERED() {
    ROrderStatus delivered = ROrderStatus.delivered();

    assertFalse(delivered.canTransitionTo(ROrderStatus.pending()));
    assertFalse(delivered.canTransitionTo(ROrderStatus.confirmed()));
    assertFalse(delivered.canTransitionTo(ROrderStatus.shipped()));
    assertFalse(delivered.canTransitionTo(ROrderStatus.cancelled()));
  }

  @Test
  @DisplayName("Should Not Allow Transitions From CANCELLED")
  void shouldNotAllowTransitionsFromCANCELLED() {
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertFalse(cancelled.canTransitionTo(ROrderStatus.pending()));
    assertFalse(cancelled.canTransitionTo(ROrderStatus.confirmed()));
    assertFalse(cancelled.canTransitionTo(ROrderStatus.shipped()));
    assertFalse(cancelled.canTransitionTo(ROrderStatus.delivered()));
  }

  @Test
  @DisplayName("Should Correctly Identify PENDING Status")
  void shouldCorrectlyIdentifyPENDINGStatus() {
    ROrderStatus pending = ROrderStatus.pending();

    assertTrue(pending.isPending());
    assertFalse(pending.isConfirmed());
    assertFalse(pending.isShipped());
    assertFalse(pending.isDelivered());
    assertFalse(pending.isCancelled());
  }

  @Test
  @DisplayName("Should Correctly Identify CONFIRMED Status")
  void shouldCorrectlyIdentifyCONFIRMEDStatus() {
    ROrderStatus confirmed = ROrderStatus.confirmed();

    assertFalse(confirmed.isPending());
    assertTrue(confirmed.isConfirmed());
    assertFalse(confirmed.isShipped());
    assertFalse(confirmed.isDelivered());
    assertFalse(confirmed.isCancelled());
  }

  @Test
  @DisplayName("Should Correctly Identify SHIPPED Status")
  void shouldCorrectlyIdentifySHIPPEDStatus() {
    ROrderStatus shipped = ROrderStatus.shipped();

    assertFalse(shipped.isPending());
    assertFalse(shipped.isConfirmed());
    assertTrue(shipped.isShipped());
    assertFalse(shipped.isDelivered());
    assertFalse(shipped.isCancelled());
  }

  @Test
  @DisplayName("Should Correctly Identify DELIVERED Status")
  void shouldCorrectlyIdentifyDELIVEREDStatus() {
    ROrderStatus delivered = ROrderStatus.delivered();

    assertFalse(delivered.isPending());
    assertFalse(delivered.isConfirmed());
    assertFalse(delivered.isShipped());
    assertTrue(delivered.isDelivered());
    assertFalse(delivered.isCancelled());
  }

  @Test
  @DisplayName("Should Correctly Identify CANCELLED Status")
  void shouldCorrectlyIdentifyCANCELLEDStatus() {
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertFalse(cancelled.isPending());
    assertFalse(cancelled.isConfirmed());
    assertFalse(cancelled.isShipped());
    assertFalse(cancelled.isDelivered());
    assertTrue(cancelled.isCancelled());
  }

  @Test
  @DisplayName("Should Identify DELIVERED As Final State")
  void shouldIdentifyDELIVEREDAsFinalState() {
    ROrderStatus delivered = ROrderStatus.delivered();

    assertTrue(delivered.isFinalState());
  }

  @Test
  @DisplayName("Should Identify CANCELLED As Final State")
  void shouldIdentifyCANCELLEDAsFinalState() {
    ROrderStatus cancelled = ROrderStatus.cancelled();

    assertTrue(cancelled.isFinalState());
  }

  @Test
  @DisplayName("Should Not Identify Non-Final States As Final")
  void shouldNotIdentifyNonFinalStatesAsFinal() {
    assertFalse(ROrderStatus.pending().isFinalState());
    assertFalse(ROrderStatus.confirmed().isFinalState());
    assertFalse(ROrderStatus.shipped().isFinalState());
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By Value")
  void shouldSupportEqualsAndHashCodeByValue() {
    ROrderStatus status1 = ROrderStatus.pending();
    ROrderStatus status2 = ROrderStatus.pending();

    assertEquals(status1, status2);
    assertEquals(status1.hashCode(), status2.hashCode());
  }

  @Test
  @DisplayName("Should Not Be Equal When Values Differ")
  void shouldNotBeEqualWhenValuesDiffer() {
    ROrderStatus pending = ROrderStatus.pending();
    ROrderStatus confirmed = ROrderStatus.confirmed();

    assertNotEquals(pending, confirmed);
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    ROrderStatus status = ROrderStatus.pending();

    assertNotNull(status.toString());
    assertFalse(status.toString().isEmpty());
    assertTrue(status.toString().contains("PENDING"));
  }
}
