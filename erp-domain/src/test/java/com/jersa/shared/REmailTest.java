package com.jersa.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("REmail Domain Test")
class REmailTest {

  final String msgNullEx = "Email cannot be null";
  final String msgBlankEx = "Email cannot be blank";
  final String msgInvalidFormatEx = "Invalid email format: ";

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Email Is Null")
  void shouldThrowIllegalArgumentExceptionWhenEmailIsNull() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> new REmail(null));
    assertEquals(msgNullEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Email Is Blank")
  void shouldThrowIllegalArgumentExceptionWhenEmailIsBlank() {
    var exceptionForEmpty = assertThrows(IllegalArgumentException.class,
        () -> new REmail(""));
    assertEquals(msgBlankEx, exceptionForEmpty.getMessage());

    var exceptionForSpace = assertThrows(IllegalArgumentException.class,
        () -> new REmail("   "));
    assertEquals(msgBlankEx, exceptionForSpace.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Email Format Is Invalid")
  void shouldThrowIllegalArgumentExceptionWhenEmailFormatIsInvalid() {
    String[] invalidEmails = {
        "plainaddress",
        "@missingusername.com",
        "username@.com",
        "username@domain.",
        "username@domain.c",
        "username@domain.tooooooolong",
        "username@domain..com",
        "username@domain.com."
    };

    for (String invalidEmail : invalidEmails) {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new REmail(invalidEmail));
      assertTrue(exception.getMessage().contains(msgInvalidFormatEx));
      assertTrue(exception.getMessage().contains(invalidEmail));
    }
  }

  @Test
  @DisplayName("Should Create REmail Successfully With Valid Email")
  void shouldCreateREmailSuccessfullyWithValidEmail() {
    String[] validEmails = {
        "user@example.com",
        "user.name@example.co.uk",
        "user+label@example.com",
        "user_name@example.com",
        "user-name@example.com",
        "user123@example.com",
        "user@subdomain.example.com"
    };

    for (String validEmail : validEmails) {
      REmail email = new REmail(validEmail);
      assertNotNull(email);
      assertEquals(validEmail, email.value());
    }
  }

  @Test
  @DisplayName("Should Create REmail Using Of Factory Method")
  void shouldCreateREmailUsingOfFactoryMethod() {
    String emailValue = "test@example.com";
    REmail email = REmail.of(emailValue);

    assertNotNull(email);
    assertEquals(emailValue, email.value());
  }

  @Test
  @DisplayName("Should Return Correct String Representation")
  void shouldReturnCorrectStringRepresentation() {
    String emailValue = "user@example.com";
    REmail email = new REmail(emailValue);

    assertEquals(emailValue, email.value());
  }

  @Test
  @DisplayName("Should Consider Two Emails Equal When They Have Same Value")
  void shouldConsiderTwoEmailsEqualWhenTheyHaveSameValue() {
    String emailValue = "user@example.com";
    REmail email1 = new REmail(emailValue);
    REmail email2 = new REmail(emailValue);

    assertEquals(email1, email2);
    assertEquals(email1.hashCode(), email2.hashCode());
  }

  @Test
  @DisplayName("Should Consider Two Emails Different When They Have Different Values")
  void shouldConsiderTwoEmailsDifferentWhenTheyHaveDifferentValues() {
    REmail email1 = new REmail("user1@example.com");
    REmail email2 = new REmail("user2@example.com");

    assertNotEquals(email1, email2);
  }

  @Test
  @DisplayName("Should Be Equal To Itself")
  void shouldBeEqualItself() {
    REmail email = new REmail("user@example.com");

    assertEquals(email, email);
  }

  @Test
  @DisplayName("Should Not Be Equal To Null Or Different Class")
  void shouldNotBeEqualNullOrDifferentClass() {
    REmail email = new REmail("user@example.com");

    assertNotEquals(null, email);
    assertNotEquals("user@example.com", email);
  }
}
