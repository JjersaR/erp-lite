package com.jersa.product;

public record RCategoryReference(String categoryId) {
  public RCategoryReference {
    if (categoryId == null) {
      throw new IllegalArgumentException("Category ID cannot be null");
    }
    if (categoryId.isBlank()) {
      throw new IllegalArgumentException("CategoryId cannot be blank");
    }
  }

  /**
   * Creates a CategoryReference from a String value.
   *
   * @param categoryId the category identifier
   * @return a new CategoryReference instance
   */
  public static RCategoryReference of(String categoryId) {
    return new RCategoryReference(categoryId);
  }
}
