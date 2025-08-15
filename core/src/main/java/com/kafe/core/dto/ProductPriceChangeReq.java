package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductPriceChangeReq {
  @NotNull @DecimalMin("0.0") public BigDecimal newPrice;
  @Size(max=200) public String reason;
}
