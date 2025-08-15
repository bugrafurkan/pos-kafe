package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class StockMovementCreateReq {
  @NotNull public Long productId;
  @NotNull public Reason reason; // MANUAL_IN, MANUAL_OUT, CORRECTION
  @NotNull @DecimalMin(value="0.000") public BigDecimal quantity; // + değer gönder
  @Size(max=255) public String note;

  public enum Reason { MANUAL_IN, MANUAL_OUT, CORRECTION }
}
