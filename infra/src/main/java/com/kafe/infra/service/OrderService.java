package com.kafe.infra.service;

import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.infra.entity.OrderEntity;
import com.kafe.infra.entity.PaymentEntity;
import com.kafe.infra.entity.TableEntity;
import com.kafe.infra.repo.OrderRepository;
import com.kafe.infra.repo.PaymentRepository;
import com.kafe.infra.repo.TableRepository;
import com.kafe.infra.repo.OrderItemRepository2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final TableRepository tableRepo;
    private final OrderItemRepository2 itemRepo;
    private final StockService stockService;

    public OrderEntity getOrder(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
    }

    @Transactional
    public void finalizePayment(Long orderId, OrderPaymentReq req) {
        OrderEntity order = getOrder(orderId);
        
        if ("PAID".equals(order.getStatus())) {
            throw new IllegalStateException("Order is already paid");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .orderId(orderId)
                .method(req.paymentType.toString())
                .amount(order.getGrandTotal())
                .status("INITIATED")
                .build();

        if ("CASH".equalsIgnoreCase(req.paymentType.toString())) {
            // CASH payment - immediate capture
            payment.setStatus("CAPTURED");
            payment.setApprovedAt(OffsetDateTime.now());
            order.setStatus("PAID");
            order.setClosedAt(OffsetDateTime.now());
            
            closeOrder(order);
        } else if ("CARD".equalsIgnoreCase(req.paymentType.toString())) {
            if (req.cardPaid != null && req.cardPaid) {
                // CARD payment - paid
                payment.setStatus("CAPTURED");
                payment.setApprovedAt(OffsetDateTime.now());
                order.setStatus("PAID");
                order.setClosedAt(OffsetDateTime.now());
                
                closeOrder(order);
            } else {
                // CARD payment - not paid
                payment.setStatus("FAILED");
                order.setStatus("PENDING_PAYMENT");
            }
        }

        paymentRepo.save(payment);
        orderRepo.save(order);
    }

    @Transactional
    public void closeOrder(OrderEntity order) {
        // Masa boşa düş
        TableEntity table = tableRepo.findById(order.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));
        table.setStatus("AVAILABLE");
        tableRepo.save(table);

        // Stok düş
        var items = itemRepo.findByOrderId(order.getId());
        for (var item : items) {
            stockService.applySaleForOrderItem(item.getProductId(), item.getQty(), order.getId());
        }

        // Siparişi kapat
        order.setStatus("PAID");
        order.setClosedAt(OffsetDateTime.now());
        orderRepo.save(order);
    }

    @Transactional
    public OrderEntity createOrder(Long tableId) {
        // Check if table is available
        TableEntity table = tableRepo.findById(tableId)
                .orElseThrow(() -> new NoSuchElementException("Table not found: " + tableId));
        
        if (!"AVAILABLE".equals(table.getStatus())) {
            throw new IllegalStateException("Table is not available: " + tableId);
        }
        
        // Set table to occupied
        table.setStatus("OCCUPIED");
        tableRepo.save(table);
        
        OrderEntity order = OrderEntity.builder()
                .tableId(tableId)
                .status("OPEN")
                .preDiscountTotal(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .subtotalExclVat(BigDecimal.ZERO)
                .vatTotal(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .openedAt(OffsetDateTime.now())
                .build();
        
        return orderRepo.save(order);
    }

    public OrderEntity getActiveOrderByTable(Long tableId) {
        return orderRepo.findActiveOrderByTableId(tableId)
                .orElse(null);
    }
}
