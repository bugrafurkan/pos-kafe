package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="product_bom")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductBomEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

  @Column(name="product_id", nullable=false) Long productId;
  @Column(name="component_product_id", nullable=false) Long componentProductId;

  @Column(name="component_qty", nullable=false, precision=14, scale=3)
  BigDecimal componentQty;
}
