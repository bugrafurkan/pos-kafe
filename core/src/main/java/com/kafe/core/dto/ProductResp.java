package com.kafe.core.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ProductResp {
  public Long id;
  public String sku;
  public String name;
  public Long categoryId;
  public BigDecimal listPrice;
  public Boolean priceIncludesVat;
  public String barcode;
  public String unit;
  public BigDecimal costPrice;
  public BigDecimal minPrice;
  public BigDecimal maxPrice;
  public Boolean active;
  public OffsetDateTime createdAt;
  public OffsetDateTime updatedAt;
}
