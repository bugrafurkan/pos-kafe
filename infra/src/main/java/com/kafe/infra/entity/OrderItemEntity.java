package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable=false)
    private Long orderId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(nullable=false, precision=12, scale=3)
    private BigDecimal qty;

    @Column(name="unit_price", nullable=false, precision=12, scale=2)
    private BigDecimal unitPrice;

    @Column(name="line_total", nullable=false, precision=12, scale=2)
    private BigDecimal lineTotal;

    @Column(name="vat_rate", nullable=false, precision=5, scale=2)
    private BigDecimal vatRate;

    @Column(name="vat_amount", nullable=false, precision=12, scale=2)
    private BigDecimal vatAmount;

    @Column(name="applied_discount_amount", nullable=false, precision=12, scale=2)
    private BigDecimal appliedDiscountAmount;
}
