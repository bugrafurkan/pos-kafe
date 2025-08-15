# Order Payment Implementation

## Overview
This document describes the implementation of the order payment flow for the POS cafe application.

## Implemented Components

### 1. Core Domain Enums
- **OrderStatus**: `OPEN`, `PENDING_PAYMENT`, `PAID`, `CANCELLED`
- **PaymentType**: `CASH`, `CARD`
- **PaymentStatus**: `INITIATED`, `CAPTURED`, `FAILED`

### 2. Entities
- **OrderEntity**: Contains tableId, status, subtotal, discountRate, total, closedAt, and items relationship
- **OrderItemEntity**: Contains order relationship, productId, productName, unitPrice, quantity, and other line item details
- **PaymentEntity**: Contains order relationship, type, status, amount, and transaction details
- **TableEntity**: Represents cafe tables with status management

### 3. Repositories
- **OrderRepository**: CRUD operations for orders with custom queries for active orders
- **OrderItemRepository**: CRUD operations for order items
- **PaymentRepository**: CRUD operations for payments
- **TableRepository**: CRUD operations for tables

### 4. Service Layer
- **OrderService**: Main business logic for order management and payment processing

### 5. Controller Layer
- **OrderController**: REST endpoints for order operations

## Payment Flow

### Cash Payment
1. User selects "Nakit" payment type
2. Payment is immediately marked as `CAPTURED`
3. Order status becomes `PAID`
4. Table is freed (status set to `AVAILABLE`)
5. Stock is reduced for all order items
6. Order is closed (closedAt timestamp set)

### Card Payment
1. User selects "Kredi Kartı" payment type
2. If `cardPaid = true`:
   - Payment marked as `CAPTURED`
   - Order status becomes `PAID`
   - Table freed and stock reduced (same as cash)
3. If `cardPaid = false`:
   - Payment marked as `FAILED`
   - Order status becomes `PENDING_PAYMENT`
   - Table remains occupied
   - Stock is not reduced

## API Endpoints

### Order Management
- `POST /orders/tables/{tableId}` - Create new order for table
- `GET /orders/tables/{tableId}/active` - Get active order for table
- `GET /orders/{orderId}` - Get order details

### Payment Processing
- `POST /orders/{orderId}/payment` - Finalize payment for order

## Request/Response Examples

### Payment Request
```json
{
  "paymentType": "CASH",
  "cardPaid": null
}
```

```json
{
  "paymentType": "CARD",
  "cardPaid": true
}
```

## Database Schema
The implementation uses the existing database schema with tables:
- `order` - Main order information
- `order_item` - Order line items
- `payment` - Payment transactions
- `cafe_table` - Table management

## Stock Integration
When an order is paid, the system automatically:
1. Calls `StockService.applySaleForOrderItem()` for each order item
2. Reduces stock quantities accordingly
3. Creates stock movement records

## Table Management
- Tables are automatically marked as `OCCUPIED` when an order is created
- Tables are automatically marked as `AVAILABLE` when an order is paid and closed

## Build Status
✅ All modules compile successfully
✅ Core business logic implemented
✅ REST API endpoints available
✅ Database entities and repositories ready
