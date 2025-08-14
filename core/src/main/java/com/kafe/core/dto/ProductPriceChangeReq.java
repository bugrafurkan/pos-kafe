package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductPriceChangeReq {
  @NotNull @DecimalMin("0.0") public BigDecimal newPrice;
  @NotBlank public String reason; // MANUAL | PROMO | CORRECTION
}
