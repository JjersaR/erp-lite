package com.jersa.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;

  @Column(name = "order_number", nullable = false, unique = true, length = 50)
  private String orderNumber;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "customer_name", nullable = false, length = 200)
  private String customerName;

  @Column(name = "created_by", nullable = false, length = 100)
  private String createdBy;

  @Column(name = "order_date", nullable = false, columnDefinition = "timestamp without time zone")
  private LocalDateTime orderDate;

  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private String status = "PENDING";

  @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "currency", nullable = false, length = 3)
  @Builder.Default
  private String currency = "USD";

  @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp without time zone")
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp without time zone")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<OrderProductEntity> items = new ArrayList<>();

  @PrePersist
  protected void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    if (orderDate == null) {
      orderDate = now;
    }
    if (createdAt == null) {
      createdAt = now;
    }
    if (updatedAt == null) {
      updatedAt = now;
    }
    if (status == null) {
      status = "PENDING";
    }
    if (currency == null) {
      currency = "USD";
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
