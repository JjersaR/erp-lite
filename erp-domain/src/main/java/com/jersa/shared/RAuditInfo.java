package com.jersa.shared;

import java.time.Instant;

public record RAuditInfo(
    String createdBy,
    Instant createdAt,
    Instant updatedAt) {
  public RAuditInfo {
    if (createdBy == null || createdBy.isBlank()) {
      throw new IllegalArgumentException("Created by cannot be null or blank");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("Created at cannot be null");
    }
    if (updatedAt == null) {
      throw new IllegalArgumentException("Updated at cannot be null");
    }
    if (updatedAt.isBefore(createdAt)) {
      throw new IllegalArgumentException("Updated at cannot be before created at");
    }
  }

  /**
   * Creates a new AuditInfo with the current timestamp for both creation and
   * update.
   *
   * @param createdBy the username who created the entity
   * @param timestamp the timestamp for creation and update
   * @return a new AuditInfo instance
   */
  public static RAuditInfo create(String createdBy, Instant timestamp) {
    return new RAuditInfo(createdBy, timestamp, timestamp);
  }

  /**
   * Updates the timestamp to the current time.
   *
   * @return a new AuditInfo instance with updated timestamp
   */
  public RAuditInfo updateTimestamp() {
    return new RAuditInfo(this.createdBy, this.createdAt, Instant.now());
  }
}
