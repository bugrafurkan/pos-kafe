package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity @Table(name="product")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;

  @Column(nullable=false, unique=true, length=64) String sku;
  @Column(nullable=false, length=200) String name;

  @Column(name="category_id", nullable=false) Long categoryId;

  @Column(name="list_price", nullable=false, precision=12, scale=2)
  BigDecimal listPrice;

  @Column(name="price_includes_vat", nullable=false)
  @Builder.Default
  Boolean priceIncludesVat = Boolean.TRUE;

  @Column(unique=true, length=64) String barcode;
  @Column(length=16) String unit;

  @Column(name="cost_price", precision=12, scale=2) BigDecimal costPrice;
  @Column(name="min_price", precision=12, scale=2) BigDecimal minPrice;
  @Column(name="max_price", precision=12, scale=2) BigDecimal maxPrice;

  @Column(nullable=false) @Builder.Default Boolean active = Boolean.TRUE;
  @Column(name="deleted_at") OffsetDateTime deletedAt;
}
