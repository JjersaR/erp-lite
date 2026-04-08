package com.jersa.persistence.mongo.documents;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "product_documents")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductInCatalogDocument {
  @Id
  private String id;

  private boolean active;

  private String categoryId;

  private String categoryName;

  private Instant createdAt;

  private Instant updatedAt;

  private String currency;

  private String description;

  private String imageUrl;

  private String name;

  private BigDecimal price;

  private String sku;

  private RProductSpecifications specifications;

  private int stock;

  private List<String> tags;
}
