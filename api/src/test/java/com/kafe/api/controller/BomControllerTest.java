package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.*;
import com.kafe.infra.service.BomService;
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
class BomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BomService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void get_returnsBom() throws Exception {
        BomResp resp = new BomResp();
        resp.productId = 1L;
        resp.lines = List.of();
        when(service.getBom(1L)).thenReturn(resp);

        mockMvc.perform(get("/products/1/bom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void upsert_returnsUpdatedBom() throws Exception {
        BomUpsertReq req = new BomUpsertReq();
        BomLineReq line = new BomLineReq();
        line.componentProductId = 2L;
        line.componentQty = new BigDecimal("0.5");
        req.lines = List.of(line);

        BomResp resp = new BomResp();
        resp.productId = 1L;
        resp.lines = List.of();
        when(service.upsertBom(eq(1L), any(BomUpsertReq.class))).thenReturn(resp);

        mockMvc.perform(put("/products/1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1));
    }
}
