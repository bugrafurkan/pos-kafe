package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity @Table(name="product_price_history")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductPriceHistoryEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;

  @Column(name="product_id", nullable=false) Long productId;

  @Column(name="old_price", precision=12, scale=2) BigDecimal oldPrice;
  @Column(name="new_price", precision=12, scale=2, nullable=false) BigDecimal newPrice;

  @Column(name="changed_at", nullable=false) @Builder.Default OffsetDateTime changedAt = OffsetDateTime.now();
  @Column(name="changed_by") Long changedBy;
  @Column(length=24) String reason;
}
