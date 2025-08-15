package com.kafe.core.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductCsvRow {
    @NotBlank public String sku;
    @NotBlank public String name;
    @NotNull @DecimalMin("0.00") public BigDecimal price;
    public String category;
    @DecimalMin("0.000") public BigDecimal stockQty;
    @DecimalMin("0.000") public BigDecimal reorderLevel;
}
