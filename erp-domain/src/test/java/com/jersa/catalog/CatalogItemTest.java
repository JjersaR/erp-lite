package com.jersa.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DisplayName("CatalogItem Domain Test")
class CatalogItemTest {

  final String msgEx = "Code cannot be null or empty";
  final String msgIdEx = "Entity ID cannot be null";

  @Test
  @DisplayName("Should Throw IllegalArgumentException When Code Is Blank or null")
  void shouldThrowIllegalArgumentExceptionWhenCodeIsBlankOrNull() {
    var targetExForNull = assertThrows(IllegalArgumentException.class,
        () -> {
          new CatalogItem(UUID.randomUUID().toString(), null, "some value", "This product is a test", 1,
              Map.of("Vale display", "Test"));
        });
    assertEquals(msgEx, targetExForNull.getMessage());

    var targetExForEmpty = assertThrows(IllegalArgumentException.class,
        () -> {
          new CatalogItem(UUID.randomUUID().toString(), "", "some value", "This product is a test", 1,
              Map.of("Vale display", "Test"));
        });
    assertEquals(msgEx, targetExForEmpty.getMessage());
  }

  @Test
  @DisplayName("Should Throw IllegalArgumentException When ID Is Null")
  void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> {
          new CatalogItem(null, "CODE001", "some value", "This product is a test", 1,
              Map.of("Vale display", "Test"));
        });
    assertEquals(msgIdEx, exception.getMessage());
  }

  @Test
  @DisplayName("Should Create CatalogItem Successfully With Valid Parameters")
  void shouldCreateCatalogItemSuccessfullyWithValidParameters() {
    String id = UUID.randomUUID().toString();
    String code = "CODE001";
    String value = "Product Value";
    String description = "Product Description";
    int displayOrder = 5;
    Map<String, Object> metadata = Map.of("key1", "value1", "key2", 123);

    CatalogItem item = new CatalogItem(id, code, value, description, displayOrder, metadata);

    assertNotNull(item);
    assertEquals(id, item.getId());
    assertEquals(code, item.getCode());
    assertEquals(value, item.getValue());
    assertEquals(description, item.getDescription());
    assertEquals(displayOrder, item.getDisplayOrder());
    assertTrue(item.isActive());
    assertNotNull(item.getMetadata());
  }

  @Test
  @DisplayName("Should Create CatalogItem With Empty Metadata When Null Is Provided")
  void shouldCreateCatalogItemWithEmptyMetadataWhenNullIsProvided() {
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE002",
        "value",
        "description",
        1,
        null);

    assertNotNull(item.getMetadata());
    assertTrue(item.getMetadata().isEmpty());
  }

  @Test
  @DisplayName("Should Create Immutable Copy of Metadata")
  void shouldCreateImmutableCopyOfMetadata() {
    Map<String, Object> originalMetadata = new HashMap<>();
    originalMetadata.put("key1", "value1");
    originalMetadata.put("key2", "value2");

    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE003",
        "value",
        "description",
        1,
        originalMetadata);

    // Modify original map
    originalMetadata.put("key3", "value3");

    // Item's metadata should remain unchanged
    assertFalse(item.getMetadata().containsKey("key3"));
    assertEquals(2, item.getMetadata().size());
  }

  @Test
  @DisplayName("Should Turn Off Status Successfully")
  void shouldTurnOffStatusSuccessfully() {
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE004",
        "value",
        "description",
        1,
        Map.of());

    assertTrue(item.isActive());
    item.turnOffStatus();
    assertFalse(item.isActive());
  }

  @Test
  @DisplayName("Should Turn On Status Successfully")
  void shouldTurnOnStatusSuccessfully() {
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE005",
        "value",
        "description",
        1,
        Map.of());

    item.turnOffStatus();
    assertFalse(item.isActive());
    item.turnOnStatus();
    assertTrue(item.isActive());
  }

  @Test
  @DisplayName("Should Return Metadata Value By Key")
  void shouldReturnMetadataValueByKey() {
    Map<String, Object> metadata = Map.of("price", 99.99, "stock", 100);
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE006",
        "value",
        "description",
        1,
        metadata);

    assertEquals(99.99, item.getMetadata("price"));
    assertEquals(100, item.getMetadata("stock"));
    assertNull(item.getMetadata("nonExistentKey"));
  }

  @Test
  @DisplayName("Should Check If Metadata Contains Key")
  void shouldCheckIfMetadataContainsKey() {
    Map<String, Object> metadata = Map.of("color", "red", "size", "M");
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE007",
        "value",
        "description",
        1,
        metadata);

    assertTrue(item.hasMetadata("color"));
    assertTrue(item.hasMetadata("size"));
    assertFalse(item.hasMetadata("weight"));
  }

  @Test
  @DisplayName("Should Return Correct String Representation")
  void shouldReturnCorrectStringRepresentation() {
    String id = UUID.randomUUID().toString();
    CatalogItem item = new CatalogItem(
        id,
        "CODE008",
        "value",
        "description",
        1,
        Map.of());

    String expectedToString = "CatalogItem{id=" + id + "}";
    assertEquals(expectedToString, item.toString());
  }

  @Test
  @DisplayName("Should Consider Two Items Equal When They Have Same Id")
  void shouldConsiderTwoItemsEqualWhenTheyHaveSameId() {
    String sameId = UUID.randomUUID().toString();

    CatalogItem item1 = new CatalogItem(
        sameId,
        "CODE009",
        "value1",
        "description1",
        1,
        Map.of("key", "value1"));

    CatalogItem item2 = new CatalogItem(
        sameId,
        "CODE010",
        "value2",
        "description2",
        2,
        Map.of("key", "value2"));

    assertEquals(item1, item2);
    assertEquals(item1.hashCode(), item2.hashCode());
  }

  @Test
  @DisplayName("Should Consider Two Items Different When They Have Different Id")
  void shouldConsiderTwoItemsDifferentWhenTheyHaveDifferentId() {
    CatalogItem item1 = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE011",
        "value",
        "description",
        1,
        Map.of());

    CatalogItem item2 = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE012",
        "value",
        "description",
        1,
        Map.of());

    assertNotEquals(item1, item2);
  }

  @Test
  @DisplayName("Should Be Equal To Itself")
  void shouldBeEqualItself() {
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE013",
        "value",
        "description",
        1,
        Map.of());

    assertEquals(item, item);
  }

  @Test
  @DisplayName("Should Not Be Equal To Null Or Different Class")
  void shouldNotBeEqualNullOrDifferentClass() {
    CatalogItem item = new CatalogItem(
        UUID.randomUUID().toString(),
        "CODE014",
        "value",
        "description",
        1,
        Map.of());

    assertNotEquals(null, item);
    assertNotEquals("some string", item);
  }
}
