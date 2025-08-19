package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService service;

    @Test
    void daily_returnsDailyReport() throws Exception {
        DailyReport report = new DailyReport();
        report.totalSales = new BigDecimal("1000.00");
        report.totalOrders = 10;
        report.totalItems = 25;
        when(service.getDailyReport(any(LocalDate.class))).thenReturn(report);

        mockMvc.perform(get("/reports/daily")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(1000.00))
                .andExpect(jsonPath("$.totalOrders").value(10))
                .andExpect(jsonPath("$.totalItems").value(25));
    }

    @Test
    void dailyProducts_returnsProductReports() throws Exception {
        ProductSalesReport report = new ProductSalesReport();
        report.productId = 1L;
        report.productName = "Test Product";
        report.quantitySold = 5L;
        report.revenue = new BigDecimal("50.00");
        when(service.getDailyProductReport(any(LocalDate.class))).thenReturn(List.of(report));

        mockMvc.perform(get("/reports/daily/products")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[0].quantitySold").value(5.0))
                .andExpect(jsonPath("$[0].revenue").value(50.00));
    }

    @Test
    void monthly_returnsMonthlyReport() throws Exception {
        MonthlyReport report = new MonthlyReport();
        report.monthlyTotal = new BigDecimal("30000.00");
        report.dailySales = List.of();
        when(service.getMonthlyReport(any(YearMonth.class))).thenReturn(report);

        mockMvc.perform(get("/reports/monthly")
                        .param("year", "2024")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyTotal").value(30000.00));
    }
}
