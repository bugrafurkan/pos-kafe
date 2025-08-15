package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable=false)
    private Long orderId;

    @Column(nullable=false, length=16)
    private String method; // CASH, CARD

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal amount;

    @Column(nullable=false, length=24)
    private String status; // INITIATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED

    @Column(name="pos_txn_id", length=64)
    private String posTxnId;

    @Column(name="approved_at")
    private OffsetDateTime approvedAt;

    @Column(name="details_json")
    private String detailsJson;
}
