package com.kafe.core.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddOrderItemReq {
    @NotNull public Long productId;
    @NotNull @DecimalMin("0.001") public BigDecimal qty; // destek: 1, 0.5 vb.
}
