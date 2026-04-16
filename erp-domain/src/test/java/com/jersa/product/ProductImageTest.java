package com.jersa.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductImage Domain Test")
class ProductImageTest {
  @Test
  @DisplayName("Should Throw IllegalArgumentException When Image URL Is Null")
  void shouldThrowIllegalArgumentExceptionWhenImageURLIsNull() {
    final String msgEx = "Image URL cannot be null";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RProductImage(null));

    assertEquals(msgEx, targetEx.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When URL Format Is Invalid")
  void shouldThrowIllegalArgumentExceptionWhenURLFormatIsInvalid() {
    String[] invalidURLs = {
        "not a url",
        "ftp://example.com/image.jpg",
        "file:///local/path/image.jpg",
        "image.jpg",
        "//example.com/image.jpg",
        ""
    };

    for (String invalidURL : invalidURLs) {
      IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
          () -> new RProductImage(invalidURL),
          "Should throw exception for: " + invalidURL);

      assertTrue(targetEx.getMessage().contains("Invalid URL") ||
          targetEx.getMessage().contains("URL must"),
          "Exception message should indicate invalid URL for: " + invalidURL);
    }
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When URL Is Not Absolute")
  void shouldThrowIllegalArgumentExceptionWhenURLIsNotAbsolute() {
    String relativeURL = "/images/product.jpg";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RProductImage(relativeURL));

    assertTrue(targetEx.getMessage().contains("URL must be absolute"));
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When URL Scheme Is Not HTTP Or HTTPS")
  void shouldThrowIllegalArgumentExceptionWhenURLSchemeIsNotHTTPOrHTTPS() {
    String ftpURL = "ftp://example.com/image.jpg";

    IllegalArgumentException targetEx = assertThrows(IllegalArgumentException.class,
        () -> new RProductImage(ftpURL));

    assertTrue(targetEx.getMessage().contains("URL must use http or https scheme"));
  }

  @Test
  @DisplayName("Should Create RProductImage With Valid HTTP URL")
  void shouldCreateRProductImageWithValidHTTPURL() {
    String validURL = "http://example.com/images/product.jpg";

    RProductImage productImage = new RProductImage(validURL);

    assertEquals(validURL, productImage.imageUrl());
  }

  @Test
  @DisplayName("Should Create RProductImage With Valid HTTPS URL")
  void shouldCreateRProductImageWithValidHTTPSURL() {
    String validURL = "https://example.com/images/product.jpg";

    RProductImage productImage = new RProductImage(validURL);

    assertEquals(validURL, productImage.imageUrl());
  }

  @Test
  @DisplayName("Should Create RProductImage Using of Method")
  void shouldCreateRProductImageUsingOfMethod() {
    String validURL = "https://s3.amazonaws.com/bucket/image.jpg";

    RProductImage productImage = RProductImage.of(validURL);

    assertEquals(validURL, productImage.imageUrl());
  }

  @Test
  @DisplayName("Should Accept Valid S3 URLs")
  void shouldAcceptValidS3URLs() {
    String[] validS3URLs = {
        "https://s3.amazonaws.com/my-bucket/images/product1.jpg",
        "https://my-bucket.s3.amazonaws.com/images/product2.png",
        "https://s3.us-east-1.amazonaws.com/bucket/folder/image.webp"
    };

    for (String validURL : validS3URLs) {
      RProductImage productImage = assertDoesNotThrow(() -> new RProductImage(validURL),
          "Should not throw exception for: " + validURL);

      assertEquals(validURL, productImage.imageUrl());
    }
  }

  @Test
  @DisplayName("Should Return Full URL Using getFullUrl Method")
  void shouldReturnFullURLUsingGetFullUrlMethod() {
    String validURL = "https://example.com/images/product.jpg";
    RProductImage productImage = new RProductImage(validURL);

    String fullUrl = productImage.getFullUrl();

    assertEquals(validURL, fullUrl);
  }

  @Test
  @DisplayName("Should Extract Filename From URL")
  void shouldExtractFilenameFromURL() {
    RProductImage productImage = new RProductImage("https://example.com/images/products/laptop.jpg");

    String filename = productImage.getFileName();

    assertEquals("laptop.jpg", filename);
  }

  @Test
  @DisplayName("Should Extract Filename From URL With Query Parameters")
  void shouldExtractFilenameFromURLWithQueryParameters() {
    RProductImage productImage = new RProductImage("https://example.com/images/product.jpg?version=1");

    String filename = productImage.getFileName();

    assertEquals("product.jpg?version=1", filename);
  }

  @Test
  @DisplayName("Should Return Full URL When Filename Cannot Be Extracted")
  void shouldReturnFullURLWhenFilenameCannotBeExtracted() {
    String urlWithoutPath = "https://example.com/";
    RProductImage productImage = new RProductImage(urlWithoutPath);

    String filename = productImage.getFileName();

    assertEquals(urlWithoutPath, filename);
  }

  @Test
  @DisplayName("Should Support Equals And HashCode By URL")
  void shouldSupportEqualsAndHashCodeByURL() {
    RProductImage productImage1 = new RProductImage("https://example.com/image.jpg");
    RProductImage productImage2 = new RProductImage("https://example.com/image.jpg");

    assertEquals(productImage1, productImage2);
    assertEquals(productImage1.hashCode(), productImage2.hashCode());
  }

  @Test
  @DisplayName("Should Not Be Equal When URLs Differ")
  void shouldNotBeEqualWhenURLsDiffer() {
    RProductImage productImage1 = new RProductImage("https://example.com/image1.jpg");
    RProductImage productImage2 = new RProductImage("https://example.com/image2.jpg");

    assertNotEquals(productImage1, productImage2);
  }

  @Test
  @DisplayName("Should Have A Non Null ToString")
  void shouldHaveANonNullToString() {
    RProductImage productImage = new RProductImage("https://example.com/image.jpg");

    assertNotNull(productImage.toString());
    assertFalse(productImage.toString().isEmpty());
    assertTrue(productImage.toString().contains("https://example.com/image.jpg"));
  }
}
