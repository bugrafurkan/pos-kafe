package com.kafe.core.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderSummaryResp {
    public Long orderId;
    public Long tableId;
    public String status; // OPEN/PENDING_PAYMENT/PAID/VOID
    public BigDecimal preDiscountTotal;
    public BigDecimal discountRate;
    public BigDecimal discountAmount;
    public BigDecimal subtotalExclVat;
    public BigDecimal vatTotal;
    public BigDecimal grandTotal;
    public OffsetDateTime openedAt;
    public OffsetDateTime closedAt;
    public List<OrderItemLine> items;

    public static class OrderItemLine {
        public Long productId;
        public String name;
        public BigDecimal qty;
        public BigDecimal unitPrice;
        public BigDecimal vatRate;
        public BigDecimal lineTotal;
    }
}
