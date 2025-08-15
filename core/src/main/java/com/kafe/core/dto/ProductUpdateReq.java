package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductUpdateReq {
  @NotBlank @Size(min=2,max=200) public String name;
  @NotNull public Long categoryId;
  public Boolean priceIncludesVat = Boolean.TRUE;
  @Size(max=64) public String barcode;
  @Size(max=16) public String unit = "pcs";
  @DecimalMin("0.0") public BigDecimal costPrice;
  public BigDecimal minPrice;
  public BigDecimal maxPrice;
  public Boolean active = Boolean.TRUE;
}
