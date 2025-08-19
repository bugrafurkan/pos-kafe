package com.kafe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafe.core.dto.*;
import com.kafe.infra.service.ModifierService;
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
class ModifierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModifierService service;

    @Autowired
    private ObjectMapper objectMapper;

    // Group tests
    @Test
    void listGroups_returnsGroups() throws Exception {
        ModifierGroupResp group = new ModifierGroupResp();
        group.id = 1L;
        group.name = "Test Group";
        group.required = false;
        group.maxSelect = 1;
        when(service.listGroups()).thenReturn(List.of(group));

        mockMvc.perform(get("/modifiers/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Group"))
                .andExpect(jsonPath("$[0].required").value(false))
                .andExpect(jsonPath("$[0].maxSelect").value(1));
    }

    @Test
    void getGroup_returnsGroup() throws Exception {
        ModifierGroupResp group = new ModifierGroupResp();
        group.id = 1L;
        group.name = "Test Group";
        when(service.getGroup(1L)).thenReturn(group);

        mockMvc.perform(get("/modifiers/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Group"));
    }

    @Test
    void createGroup_returnsCreatedGroup() throws Exception {
        ModifierGroupCreateReq req = new ModifierGroupCreateReq();
        req.name = "New Group";
        req.required = false;
        req.maxSelect = 1;

        ModifierGroupResp resp = new ModifierGroupResp();
        resp.id = 1L;
        resp.name = "New Group";
        when(service.createGroup(any(ModifierGroupCreateReq.class))).thenReturn(resp);

        mockMvc.perform(post("/modifiers/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Group"));
    }

    @Test
    void updateGroup_returnsUpdatedGroup() throws Exception {
        ModifierGroupUpdateReq req = new ModifierGroupUpdateReq();
        req.name = "Updated Group";
        req.required = true;
        req.maxSelect = 2;

        ModifierGroupResp resp = new ModifierGroupResp();
        resp.id = 1L;
        resp.name = "Updated Group";
        when(service.updateGroup(eq(1L), any(ModifierGroupUpdateReq.class))).thenReturn(resp);

        mockMvc.perform(put("/modifiers/groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Group"));
    }

    @Test
    void deleteGroup_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/modifiers/groups/1"))
                .andExpect(status().isNoContent());
    }

    // Option tests
    @Test
    void listOptions_returnsOptions() throws Exception {
        ModifierOptionResp option = new ModifierOptionResp();
        option.id = 1L;
        option.groupId = 1L;
        option.name = "Test Option";
        option.priceDelta = new BigDecimal("2.00");
        when(service.listOptions(1L)).thenReturn(List.of(option));

        mockMvc.perform(get("/modifiers/groups/1/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Option"))
                .andExpect(jsonPath("$[0].priceDelta").value(2.00));
    }

    @Test
    void createOption_returnsCreatedOption() throws Exception {
        ModifierOptionCreateReq req = new ModifierOptionCreateReq();
        req.name = "New Option";
        req.priceDelta = new BigDecimal("1.50");

        ModifierOptionResp resp = new ModifierOptionResp();
        resp.id = 1L;
        resp.groupId = 1L;
        resp.name = "New Option";
        when(service.createOption(eq(1L), any(ModifierOptionCreateReq.class))).thenReturn(resp);

        mockMvc.perform(post("/modifiers/groups/1/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.groupId").value(1))
                .andExpect(jsonPath("$.name").value("New Option"));
    }

    @Test
    void updateOption_returnsUpdatedOption() throws Exception {
        ModifierOptionUpdateReq req = new ModifierOptionUpdateReq();
        req.name = "Updated Option";
        req.priceDelta = new BigDecimal("3.00");

        ModifierOptionResp resp = new ModifierOptionResp();
        resp.id = 1L;
        resp.name = "Updated Option";
        when(service.updateOption(eq(1L), any(ModifierOptionUpdateReq.class))).thenReturn(resp);

        mockMvc.perform(put("/modifiers/options/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Option"));
    }

    @Test
    void deleteOption_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/modifiers/options/1"))
                .andExpect(status().isNoContent());
    }

    // Product link tests
    @Test
    void link_returnsLink() throws Exception {
        ProductModifierLinkResp resp = new ProductModifierLinkResp();
        resp.productId = 1L;
        resp.groupId = 1L;
        when(service.linkProductGroup(1L, 1L)).thenReturn(resp);

        mockMvc.perform(post("/products/1/modifiers/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.groupId").value(1));
    }

    @Test
    void unlink_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/products/1/modifiers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductModifiers_returnsModifiers() throws Exception {
        ProductModifiersResp resp = new ProductModifiersResp();
        resp.productId = 1L;
        resp.groups = List.of();
        when(service.getProductModifiers(1L)).thenReturn(resp);

        mockMvc.perform(get("/products/1/modifiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1));
    }
}
