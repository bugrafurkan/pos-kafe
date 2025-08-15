package com.kafe.core.dto;

import jakarta.validation.constraints.NotBlank;

public class FinalizePaymentReq {
    @NotBlank public String paymentType; // CASH | CARD
    public Boolean cardPaid;             // CARD seçildiyse: true/false
}
