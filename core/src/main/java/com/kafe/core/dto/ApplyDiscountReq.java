package com.kafe.core.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ApplyDiscountReq {
    // yüzde (0..1). Örn: 0.10 = %10
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0")
    public BigDecimal discountRate;
}
