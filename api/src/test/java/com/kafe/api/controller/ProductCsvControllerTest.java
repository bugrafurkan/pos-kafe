package com.kafe.api.controller;

import com.kafe.core.dto.CsvImportResult;
import com.kafe.infra.service.ProductCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductCsvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCsvService service;

    @Test
    void exportCsv_returnsCsvFile() throws Exception {
        byte[] csvData = "sku,name,price,category,stockQty,reorderLevel\nLATTE-12OZ,Latte 12oz,9.50,Coffee,10.0,5.0".getBytes();
        when(service.exportProducts()).thenReturn(csvData);

        mockMvc.perform(get("/products/csv/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=products.csv"))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void importCsv_returnsImportResult() throws Exception {
        CsvImportResult result = new CsvImportResult();
        result.created = 5;
        result.errors = 1;
        result.totalRows = 6;
        when(service.importProducts(any())).thenReturn(result);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                "sku,name,price,category,stockQty,reorderLevel\nLATTE-12OZ,Latte 12oz,9.50,Coffee,10.0,5.0".getBytes()
        );

        mockMvc.perform(multipart("/products/csv/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(5))
                .andExpect(jsonPath("$.errors").value(1))
                .andExpect(jsonPath("$.totalRows").value(6));
    }
}
