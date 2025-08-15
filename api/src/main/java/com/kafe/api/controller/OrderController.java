package com.kafe.api.controller;

import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.infra.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Void> finalizePayment(@PathVariable Long orderId, @RequestBody OrderPaymentReq req) {
        orderService.finalizePayment(orderId, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PostMapping("/tables/{tableId}")
    public ResponseEntity<?> createOrder(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.createOrder(tableId));
    }

    @GetMapping("/tables/{tableId}/active")
    public ResponseEntity<?> getActiveOrderByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getActiveOrderByTable(tableId));
    }
}
