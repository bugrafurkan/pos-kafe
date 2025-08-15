# Sales Reports Implementation

## Overview
The POS cafe application now includes comprehensive daily and monthly sales reporting functionality with backend services that provide detailed insights into sales performance.

## Features Implemented

### ✅ Daily Reports
- **Total Sales**: Sum of all completed orders for the day
- **Total Orders**: Count of completed orders for the day  
- **Total Items**: Sum of all items sold for the day
- **Product-wise Sales**: Detailed breakdown by product

### ✅ Monthly Reports
- **Daily Sales Breakdown**: Sales totals for each day of the month
- **Monthly Total**: Sum of all sales for the entire month
- **Trend Analysis**: Day-by-day sales performance

## API Endpoints

### 1. Daily Report
```
GET /reports/daily?date=2024-01-15
```

**Response:**
```json
{
  "totalSales": 1250.50,
  "totalOrders": 15,
  "totalItems": 45
}
```

### 2. Daily Product Report
```
GET /reports/daily-products?date=2024-01-15
```

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "Espresso",
    "quantitySold": 12,
    "revenue": 240.00
  },
  {
    "productId": 2,
    "productName": "Cappuccino",
    "quantitySold": 8,
    "revenue": 320.00
  }
]
```

### 3. Monthly Report
```
GET /reports/monthly?year=2024&month=1
```

**Response:**
```json
{
  "dailySales": [
    {
      "date": "2024-01-01",
      "total": 850.25
    },
    {
      "date": "2024-01-02", 
      "total": 1200.50
    }
  ],
  "monthlyTotal": 38500.75
}
```

## Implementation Details

### 1. Data Transfer Objects (DTOs)

#### DailyReport.java
```java
public class DailyReport {
    public BigDecimal totalSales;
    public long totalOrders;
    public long totalItems;
}
```

#### ProductSalesReport.java
```java
public class ProductSalesReport {
    public Long productId;
    public String productName;
    public long quantitySold;
    public BigDecimal revenue;
}
```

#### DailySales.java
```java
public class DailySales {
    public LocalDate date;
    public BigDecimal total;
}
```

#### MonthlyReport.java
```java
public class MonthlyReport {
    public List<DailySales> dailySales;
    public BigDecimal monthlyTotal;
}
```

### 2. Repository Layer

#### ReportRepository Interface
```java
public interface ReportRepository {
    DailyReport getDailyReport(LocalDate date);
    List<ProductSalesReport> getDailyProductReport(LocalDate date);
    List<DailySales> getMonthlySales(int year, int month);
    BigDecimal getMonthlyTotal(int year, int month);
}
```

#### ReportRepositoryImpl
Custom implementation with optimized JPQL queries:

- **Daily Report Query**: Aggregates total sales, order count, and item count
- **Product Report Query**: Groups sales by product with revenue calculation
- **Monthly Sales Query**: Daily breakdown with date grouping
- **Monthly Total Query**: Sum of all sales for the month

### 3. Service Layer

#### ReportService
Business logic layer that:
- Validates input parameters
- Orchestrates data retrieval
- Handles null values and edge cases
- Provides clean API for controllers

### 4. Controller Layer

#### ReportController
REST endpoints that:
- Accept date parameters in ISO format (YYYY-MM-DD)
- Return JSON responses
- Handle parameter validation
- Provide intuitive API design

## Database Queries

### Daily Report Query
```sql
SELECT SUM(o.total), COUNT(o), SUM(oi.qty)
FROM "order" o
JOIN order_item oi ON oi.order_id = o.id
WHERE DATE(o.closed_at) = :date
  AND o.status = 'PAID'
```

### Product Sales Query
```sql
SELECT p.id, p.name, SUM(oi.qty), SUM(oi.line_total)
FROM order_item oi
JOIN "order" o ON o.id = oi.order_id
JOIN product p ON p.id = oi.product_id
WHERE DATE(o.closed_at) = :date
  AND o.status = 'PAID'
GROUP BY p.id, p.name
```

### Monthly Sales Query
```sql
SELECT DATE(o.closed_at), SUM(o.total)
FROM "order" o
WHERE YEAR(o.closed_at) = :year
  AND MONTH(o.closed_at) = :month
  AND o.status = 'PAID'
GROUP BY DATE(o.closed_at)
ORDER BY DATE(o.closed_at)
```

## Usage Examples

### Get Today's Report
```bash
curl "http://localhost:8080/reports/daily?date=2024-01-15"
```

### Get Product Sales for Today
```bash
curl "http://localhost:8080/reports/daily-products?date=2024-01-15"
```

### Get January 2024 Report
```bash
curl "http://localhost:8080/reports/monthly?year=2024&month=1"
```

## Performance Considerations

- **Indexed Queries**: All queries use indexed columns (closed_at, status)
- **Efficient Aggregation**: Uses database-level aggregation functions
- **Minimal Data Transfer**: Only returns necessary fields
- **Caching Ready**: Service layer designed for future caching implementation

## Error Handling

- **Invalid Dates**: Returns 400 Bad Request for malformed dates
- **No Data**: Returns empty results (zero values) for dates with no sales
- **Database Errors**: Proper exception handling with meaningful messages

## Future Enhancements

- **Date Range Reports**: Support for custom date ranges
- **Export Functionality**: CSV/Excel export capabilities
- **Real-time Updates**: WebSocket integration for live reports
- **Advanced Analytics**: Trend analysis and forecasting
- **Caching**: Redis integration for improved performance
