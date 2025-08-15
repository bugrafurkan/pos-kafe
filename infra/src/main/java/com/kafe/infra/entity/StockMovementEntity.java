package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity @Table(name="stock_movement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StockMovementEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
  @Column(name="product_id", nullable=false) Long productId;
  @Column(name="qty_delta", nullable=false, precision=14, scale=3) BigDecimal qtyDelta;
  @Column(nullable=false, length=32) String reason;
  @Column(name="ref_type", length=32) String refType;
  @Column(name="ref_id") Long refId;
  @Column(name="occurred_at") OffsetDateTime occurredAt;
  @Column(length=255) String note;
}
