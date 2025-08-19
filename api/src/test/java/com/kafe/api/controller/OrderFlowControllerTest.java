package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.*;
import com.kafe.infra.service.OrderFlowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderFlowService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_returnsOrderSummary() throws Exception {
        CreateOrderReq req = new CreateOrderReq();
        req.tableId = 1L;

        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = 1L;
        resp.tableId = 1L;
        resp.status = "OPEN";
        when(service.createOrder(any(CreateOrderReq.class))).thenReturn(resp);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void addItem_returnsUpdatedOrderSummary() throws Exception {
        AddOrderItemReq req = new AddOrderItemReq();
        req.productId = 1L;
        req.qty = new BigDecimal("2");

        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = 1L;
        resp.grandTotal = new BigDecimal("20.00");
        when(service.addItem(eq(1L), any(AddOrderItemReq.class))).thenReturn(resp);

        mockMvc.perform(post("/orders/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.grandTotal").value(20.00));
    }

    @Test
    void discount_returnsUpdatedOrderSummary() throws Exception {
        ApplyDiscountReq req = new ApplyDiscountReq();
        req.discountRate = new BigDecimal("0.10");

        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = 1L;
        resp.discountRate = new BigDecimal("0.10");
        when(service.applyDiscount(eq(1L), any(ApplyDiscountReq.class))).thenReturn(resp);

        mockMvc.perform(post("/orders/1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.discountRate").value(0.10));
    }

    @Test
    void goPayment_returnsOrderSummary() throws Exception {
        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = 1L;
        resp.status = "PENDING_PAYMENT";
        when(service.goPayment(1L)).thenReturn(resp);

        mockMvc.perform(post("/orders/1/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    void finalizePayment_returnsOrderSummary() throws Exception {
        FinalizePaymentReq req = new FinalizePaymentReq();
        req.paymentType = "CASH";
        req.cardPaid = true;

        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = 1L;
        resp.status = "CLOSED";
        when(service.finalizePayment(eq(1L), any(FinalizePaymentReq.class))).thenReturn(resp);

        mockMvc.perform(post("/orders/1/finalize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}
