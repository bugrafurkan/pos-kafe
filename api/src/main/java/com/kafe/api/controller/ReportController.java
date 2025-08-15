package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/daily")
    public DailyReport daily(@RequestParam String date) {
        return service.getDailyReport(LocalDate.parse(date));
    }

    @GetMapping("/daily-products")
    public List<ProductSalesReport> dailyProducts(@RequestParam String date) {
        return service.getDailyProductReport(LocalDate.parse(date));
    }

    @GetMapping("/monthly")
    public MonthlyReport monthly(@RequestParam int year, @RequestParam int month) {
        return service.getMonthlyReport(YearMonth.of(year, month));
    }
}
