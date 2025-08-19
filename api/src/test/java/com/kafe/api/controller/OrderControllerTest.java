package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.core.domain.PaymentType;
import com.kafe.infra.service.OrderService;
import com.kafe.infra.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void finalizePayment_returnsOk() throws Exception {
        OrderPaymentReq req = new OrderPaymentReq();
        req.paymentType = PaymentType.CASH;

        mockMvc.perform(post("/orders/1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getOrder_returnsOrder() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .tableId(1L)
                .status("OPEN")
                .build();
        when(service.getOrder(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createOrder_returnsOrder() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .tableId(1L)
                .status("OPEN")
                .build();
        when(service.createOrder(1L)).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .param("tableId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getActiveOrderByTable_returnsOrder() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .tableId(1L)
                .status("OPEN")
                .build();
        when(service.getActiveOrderByTable(1L)).thenReturn(order);

        mockMvc.perform(get("/tables/1/active-order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }
}
