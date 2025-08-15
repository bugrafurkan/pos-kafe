package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="order_item")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItemEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id", nullable=false)
    OrderEntity order;

    @Column(name="product_id", nullable=false)
    Long productId;

    @Column(name="product_name", nullable=false, length=200)
    String productName;

    @Column(name="unit_price", nullable=false, precision=12, scale=2)
    BigDecimal unitPrice;

    @Column(nullable=false, precision=12, scale=3)
    BigDecimal qty;

    @Column(name="line_total", nullable=false, precision=12, scale=2)
    BigDecimal lineTotal;

    @Column(name="vat_rate", nullable=false, precision=5, scale=2)
    @Builder.Default
    BigDecimal vatRate = BigDecimal.ZERO;

    @Column(name="vat_amount", nullable=false, precision=12, scale=2)
    @Builder.Default
    BigDecimal vatAmount = BigDecimal.ZERO;

    @Column(name="applied_discount_amount", nullable=false, precision=12, scale=2)
    @Builder.Default
    BigDecimal appliedDiscountAmount = BigDecimal.ZERO;
}
