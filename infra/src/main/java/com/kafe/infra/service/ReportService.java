package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.repo.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository repo;

    public DailyReport getDailyReport(LocalDate date) {
        return repo.getDailyReport(date);
    }

    public List<ProductSalesReport> getDailyProductReport(LocalDate date) {
        return repo.getDailyProductReport(date);
    }

    public MonthlyReport getMonthlyReport(YearMonth month) {
        MonthlyReport report = new MonthlyReport();
        report.dailySales = repo.getMonthlySales(month.getYear(), month.getMonthValue());
        report.monthlyTotal = repo.getMonthlyTotal(month.getYear(), month.getMonthValue());
        return report;
    }
}
