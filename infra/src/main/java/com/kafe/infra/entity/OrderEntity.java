package com.kafe.infra.entity;

import com.kafe.core.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name="\"order\"")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    Long id;

    @Column(name="table_id", nullable=false) 
    Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    @Builder.Default
    OrderStatus status = OrderStatus.OPEN;

    @Column(name="pre_discount_total", nullable=false, precision=12, scale=2)
    @Builder.Default
    BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name="discount_rate", precision=6, scale=3)
    BigDecimal discountRate;

    @Column(name="grand_total", nullable=false, precision=12, scale=2)
    @Builder.Default
    BigDecimal total = BigDecimal.ZERO;

    @Column(name="opened_at", nullable=false)
    @Builder.Default
    LocalDateTime openedAt = LocalDateTime.now();

    @Column(name="closed_at")
    LocalDateTime closedAt;

    @Column(length=255)
    String note;

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    List<OrderItemEntity> items;
}
