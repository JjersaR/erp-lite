package com.jersa.shared;

import java.util.regex.Pattern;

public record REmail(String value) {
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

  public REmail {
    if (value == null) {
      throw new IllegalArgumentException("Email cannot be null");
    }
    if (value.isBlank()) {
      throw new IllegalArgumentException("Email cannot be blank");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format: " + value);
    }
  }

  /**
   * Creates an Email from a String value.
   *
   * @param value the email address
   * @return a new Email instance
   */
  public static REmail of(String value) {
    return new REmail(value);
  }
}
