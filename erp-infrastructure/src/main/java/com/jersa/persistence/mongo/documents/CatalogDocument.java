package com.jersa.persistence.mongo.documents;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "catalogs")
public class CatalogDocument {
  @Id
  private String id;

  private boolean active;

  private ECatalogType catalogType;

  private Instant createdAt;

  private Instant updatedAt;

  private String description;

  private String name;

  private List<RCatalogItem> items;
}
