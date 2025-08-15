package com.kafe.infra.repo.impl;

import com.kafe.core.dto.*;
import com.kafe.infra.repo.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public DailyReport getDailyReport(LocalDate date) {
        Object[] row = (Object[]) em.createQuery("""
            SELECT SUM(o.total), COUNT(o), SUM(oi.qty)
            FROM OrderEntity o
            JOIN o.items oi
            WHERE DATE(o.closedAt) = :date
              AND o.status = 'PAID'
        """)
        .setParameter("date", date)
        .getSingleResult();

        DailyReport report = new DailyReport();
        report.totalSales = row[0] != null ? (BigDecimal) row[0] : BigDecimal.ZERO;
        report.totalOrders = row[1] != null ? (Long) row[1] : 0L;
        report.totalItems = row[2] != null ? ((BigDecimal) row[2]).longValue() : 0L;
        return report;
    }

    @Override
    public List<ProductSalesReport> getDailyProductReport(LocalDate date) {
        return em.createQuery("""
            SELECT new com.kafe.core.dto.ProductSalesReport(
                p.id, p.name, SUM(oi.qty), SUM(oi.lineTotal)
            )
            FROM OrderItemEntity oi
            JOIN oi.order o
            JOIN ProductEntity p ON p.id = oi.productId
            WHERE DATE(o.closedAt) = :date
              AND o.status = 'PAID'
            GROUP BY p.id, p.name
        """, ProductSalesReport.class)
        .setParameter("date", date)
        .getResultList();
    }

    @Override
    public List<DailySales> getMonthlySales(int year, int month) {
        return em.createQuery("""
            SELECT new com.kafe.core.dto.DailySales(
                DATE(o.closedAt), SUM(o.total)
            )
            FROM OrderEntity o
            WHERE YEAR(o.closedAt) = :year
              AND MONTH(o.closedAt) = :month
              AND o.status = 'PAID'
            GROUP BY DATE(o.closedAt)
            ORDER BY DATE(o.closedAt)
        """, DailySales.class)
        .setParameter("year", year)
        .setParameter("month", month)
        .getResultList();
    }

    @Override
    public BigDecimal getMonthlyTotal(int year, int month) {
        BigDecimal result = em.createQuery("""
            SELECT SUM(o.total)
            FROM OrderEntity o
            WHERE YEAR(o.closedAt) = :year
              AND MONTH(o.closedAt) = :month
              AND o.status = 'PAID'
        """, BigDecimal.class)
        .setParameter("year", year)
        .setParameter("month", month)
        .getSingleResult();
        
        return result != null ? result : BigDecimal.ZERO;
    }
}
