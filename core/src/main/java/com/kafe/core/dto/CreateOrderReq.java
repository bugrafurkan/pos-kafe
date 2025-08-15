package com.kafe.core.dto;

import jakarta.validation.constraints.NotNull;

public class CreateOrderReq {
    @NotNull public Long tableId;
    public String note;
}
