package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ModifierOptionCreateReq {
  @NotBlank @Size(max=120) public String name;
  @NotNull public BigDecimal priceDelta; // +/-
  @Size(max=32) public String skuSuffix;
}
