package com.kafe.core.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class StockMovementResp {
  public Long id;
  public Long productId;
  public BigDecimal qtyDelta; // (+/-)
  public String reason;
  public String refType;
  public Long refId;
  public OffsetDateTime occurredAt;
  public String note;
}
