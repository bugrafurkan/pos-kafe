package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.OrderFlowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pos")
@RequiredArgsConstructor
public class OrderFlowController {

    private final OrderFlowService svc;

    @PostMapping("/orders")
    public OrderSummaryResp create(@Valid @RequestBody CreateOrderReq req) {
        return svc.createOrder(req);
    }

    @PostMapping("/orders/{orderId}/items")
    public OrderSummaryResp addItem(@PathVariable Long orderId, @Valid @RequestBody AddOrderItemReq req) {
        return svc.addItem(orderId, req);
    }

    @PutMapping("/orders/{orderId}/discount")
    public OrderSummaryResp discount(@PathVariable Long orderId, @Valid @RequestBody ApplyDiscountReq req) {
        return svc.applyDiscount(orderId, req);
    }

    @PostMapping("/orders/{orderId}/go-payment")
    public OrderSummaryResp goPayment(@PathVariable Long orderId, @RequestBody(required=false) GoPaymentReq req) {
        return svc.goPayment(orderId);
    }

    @PostMapping("/orders/{orderId}/finalize-payment")
    public OrderSummaryResp finalizePayment(@PathVariable Long orderId, @Valid @RequestBody FinalizePaymentReq req) {
        return svc.finalizePayment(orderId, req);
    }
}
