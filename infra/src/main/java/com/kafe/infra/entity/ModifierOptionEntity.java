package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="product_modifier_option")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ModifierOptionEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;

  @Column(name="group_id", nullable=false) Long groupId;
  @Column(nullable=false, length=120) String name;
  @Column(name="price_delta", nullable=false, precision=12, scale=2) BigDecimal priceDelta;
  @Column(name="sku_suffix", length=32) String skuSuffix;
}
