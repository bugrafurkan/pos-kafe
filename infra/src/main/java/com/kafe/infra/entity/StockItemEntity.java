package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="stock_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockItemEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;

  @Column(name="product_id", nullable=false, unique=true) Long productId;

  @Column(name="current_qty", nullable=false, precision=14, scale=3)
  BigDecimal currentQty;

  @Column(name="reorder_level", nullable=false, precision=14, scale=3)
  BigDecimal reorderLevel;
}
