package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.*;
import com.kafe.infra.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.kafe.core.dto.PagedResp;
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
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsPagedProducts() throws Exception {
        ProductResp product = new ProductResp();
        product.id = 1L;
        product.name = "Test Product";
        PagedResp<ProductResp> resp = new PagedResp<>();
        resp.content = List.of(product);
        resp.totalElements = 1L;
        when(service.search(anyString(), any(), any(), anyInt(), anyInt())).thenReturn(resp);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void get_returnsProduct() throws Exception {
        ProductResp product = new ProductResp();
        product.id = 1L;
        product.name = "Test Product";
        when(service.get(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void create_returnsCreatedProduct() throws Exception {
        ProductCreateReq req = new ProductCreateReq();
        req.name = "New Product";
        req.costPrice = new BigDecimal("10.00");

        ProductResp resp = new ProductResp();
        resp.id = 1L;
        resp.name = "New Product";
        when(service.create(any(ProductCreateReq.class))).thenReturn(resp);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    void update_returnsUpdatedProduct() throws Exception {
        ProductUpdateReq req = new ProductUpdateReq();
        req.name = "Updated Product";
        req.costPrice = new BigDecimal("15.00");

        ProductResp resp = new ProductResp();
        resp.id = 1L;
        resp.name = "Updated Product";
        when(service.update(eq(1L), any(ProductUpdateReq.class))).thenReturn(resp);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePrice_returnsUpdatedProduct() throws Exception {
        ProductPriceChangeReq req = new ProductPriceChangeReq();
        req.newPrice = new BigDecimal("20.00");

        ProductResp resp = new ProductResp();
        resp.id = 1L;
        resp.listPrice = new BigDecimal("20.00");
        when(service.changePrice(eq(1L), any(ProductPriceChangeReq.class), isNull())).thenReturn(resp);

        mockMvc.perform(patch("/products/1/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listPrice").value(20.00));
    }
}
