package com.kafe.core.domain;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StockMovement {
    private Long id;
    private Long productId;
    private StockMovementType movementType;
    private BigDecimal quantity;
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();
}
