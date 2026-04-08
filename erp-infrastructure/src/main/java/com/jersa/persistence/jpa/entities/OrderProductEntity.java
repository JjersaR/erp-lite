package com.jersa.persistence.jpa.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductEntity {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;

  // FK -> orders.id (ON DELETE CASCADE, ON UPDATE NO ACTION)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_products_order", foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES orders(id) "
      +
      "ON UPDATE NO ACTION ON DELETE CASCADE"))
  private OrderEntity order;

  // FK -> products.id (ON DELETE RESTRICT, ON UPDATE NO ACTION)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_products_product", foreignKeyDefinition = "FOREIGN KEY (product_id) REFERENCES products(id) "
      +
      "ON UPDATE NO ACTION ON DELETE RESTRICT"))
  private ProductEntity product;

  /** Snapshot del nombre del producto al momento de la compra. */
  @Column(name = "product_name", nullable = false, length = 200)
  private String productName;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
  private BigDecimal subtotal;

  @PrePersist
  void prePersist() {
    if (unitPrice != null && quantity != null && subtotal == null) {
      subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity.longValue()));
    }
    if (product != null && (productName == null || productName.isBlank())) {
      productName = product.getName();
    }
  }
}
