package com.kafe.infra.entity;

import com.kafe.core.domain.PaymentStatus;
import com.kafe.core.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="payment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id", nullable=false)
    OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name="method", nullable=false)
    PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    PaymentStatus status;

    @Column(nullable=false, precision=12, scale=2)
    BigDecimal amount;

    @Column(name="approved_at")
    LocalDateTime approvedAt;

    @Column(name="pos_txn_id", length=64)
    String posTxnId;

    @Column(name="details_json", columnDefinition="jsonb")
    String detailsJson;
}
