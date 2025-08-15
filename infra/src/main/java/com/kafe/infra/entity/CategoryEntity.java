package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="product_category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
  
  @Column(nullable=false, unique=true, length=120) String name;
  
  @Column(name="vat_rate", nullable=false, precision=5, scale=2)
  @Builder.Default
  BigDecimal vatRate = BigDecimal.ZERO;
}
