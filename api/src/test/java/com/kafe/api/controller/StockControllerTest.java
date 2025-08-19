package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.StockItemResp;
import com.kafe.core.dto.StockMovementCreateReq;
import com.kafe.core.dto.StockMovementResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.kafe.infra.service.StockManualService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockManualService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsPagedStockItems() throws Exception {
        StockItemResp item = new StockItemResp();
        item.productId = 1L;
        item.productName = "Test Product";
        item.currentQty = new BigDecimal("10.0");
        Page<StockItemResp> page = new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1);
        when(service.listStock(anyString(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productId").value(1))
                .andExpect(jsonPath("$.content[0].productName").value("Test Product"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void movements_returnsPagedMovements() throws Exception {
        StockMovementResp movement = new StockMovementResp();
        movement.id = 1L;
        movement.productId = 1L;
        movement.qtyDelta = new BigDecimal("5.0");
        movement.reason = "MANUAL_IN";
        Page<StockMovementResp> page = new PageImpl<>(List.of(movement), PageRequest.of(0, 10), 1);
        when(service.listMovements(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/stock/1/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].productId").value(1))
                .andExpect(jsonPath("$.content[0].qtyDelta").value(5.0))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void create_returnsMovement() throws Exception {
        StockMovementCreateReq req = new StockMovementCreateReq();
        req.quantity = new BigDecimal("5.0");
        req.reason = StockMovementCreateReq.Reason.MANUAL_IN;

        StockMovementResp resp = new StockMovementResp();
        resp.id = 1L;
        resp.productId = 1L;
        resp.qtyDelta = new BigDecimal("5.0");
        resp.reason = "MANUAL_IN";
        when(service.createMovement(any(StockMovementCreateReq.class))).thenReturn(resp);

        mockMvc.perform(post("/stock/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.qtyDelta").value(5.0));
    }

    @Test
    void setReorderLevel_returnsStockItem() throws Exception {
        StockItemResp resp = new StockItemResp();
        resp.productId = 1L;
        resp.reorderLevel = new BigDecimal("5.0");
        when(service.setReorderLevel(1L, new BigDecimal("5.0"))).thenReturn(resp);

        mockMvc.perform(put("/stock/1/reorder-level")
                        .param("level", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.reorderLevel").value(5.0));
    }
}
