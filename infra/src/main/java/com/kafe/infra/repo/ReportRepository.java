package com.kafe.infra.repo;

import com.kafe.core.dto.ProductSalesReport;
import com.kafe.core.dto.DailySales;
import com.kafe.core.dto.DailyReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {
    DailyReport getDailyReport(LocalDate date);
    List<ProductSalesReport> getDailyProductReport(LocalDate date);
    List<DailySales> getMonthlySales(int year, int month);
    BigDecimal getMonthlyTotal(int year, int month);
}
