package com.kafe.core.dto;

import java.math.BigDecimal;

public class StockItemResp {
  public Long productId;
  public String sku;
  public String productName;
  public BigDecimal currentQty;
  public BigDecimal reorderLevel;
  public Boolean belowReorder;
}
