package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity @Table(name="stock_movement")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockMovementEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
  public Long id;
  
  @Column(name="product_id", nullable=false) 
  public Long productId;
  
  @Column(name="qty_delta", nullable=false, precision=14, scale=3) 
  public BigDecimal qtyDelta;
  
  @Column(nullable=false, length=32) 
  public String reason;
  
  @Column(name="ref_type", length=32) 
  public String refType;
  
  @Column(name="ref_id") 
  public Long refId;
  
  @Column(name="occurred_at") 
  public OffsetDateTime occurredAt;
  
  @Column(length=255) 
  public String note;
}
