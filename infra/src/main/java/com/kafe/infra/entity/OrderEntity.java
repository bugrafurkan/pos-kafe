package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "\"order\"")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="table_id")
    private Long tableId;

    @Column(nullable=false, length=24)
    private String status; // OPEN, PENDING_PAYMENT, PAID, VOID

    @Column(name="pre_discount_total", precision=12, scale=2)
    private BigDecimal preDiscountTotal;

    @Column(name="discount_rate", precision=6, scale=3)
    private BigDecimal discountRate;

    @Column(name="discount_amount", precision=12, scale=2)
    private BigDecimal discountAmount;

    @Column(name="subtotal_excl_vat", precision=12, scale=2)
    private BigDecimal subtotalExclVat;

    @Column(name="vat_total", precision=12, scale=2)
    private BigDecimal vatTotal;

    @Column(name="grand_total", precision=12, scale=2)
    private BigDecimal grandTotal;

    @Column(name="opened_at")
    private OffsetDateTime openedAt;

    @Column(name="closed_at")
    private OffsetDateTime closedAt;

    private String note;
}
