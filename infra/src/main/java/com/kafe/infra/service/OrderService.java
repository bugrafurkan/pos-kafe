package com.kafe.infra.service;

import com.kafe.core.domain.OrderStatus;
import com.kafe.core.domain.PaymentStatus;
import com.kafe.core.domain.PaymentType;
import com.kafe.core.domain.TableStatus;
import com.kafe.core.domain.StockMovement;
import com.kafe.core.domain.StockMovementType;
import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.infra.entity.OrderEntity;
import com.kafe.infra.entity.PaymentEntity;
import com.kafe.infra.entity.TableEntity;
import com.kafe.infra.entity.ProductEntity;
import com.kafe.infra.repo.OrderRepository;
import com.kafe.infra.repo.PaymentRepository;
import com.kafe.infra.repo.TableRepository;
import com.kafe.infra.repo.ProductRepository;
import com.kafe.infra.repo.StockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final TableRepository tableRepo;
    private final ProductRepository productRepo;
    private final StockMovementRepository stockMovementRepo;
    private final StockService stockService;

    public OrderEntity getOrder(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
    }

    @Transactional
    public void finalizePayment(Long orderId, OrderPaymentReq req) {
        OrderEntity order = getOrder(orderId);
        
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .type(req.paymentType)
                .amount(order.getTotal())
                .status(PaymentStatus.INITIATED)
                .build();

        if (req.paymentType == PaymentType.CASH) {
            // CASH payment - immediate capture
            payment.setStatus(PaymentStatus.CAPTURED);
            payment.setApprovedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.PAID);
            order.setClosedAt(LocalDateTime.now());
            
            closeOrder(order);
        } else if (req.paymentType == PaymentType.CARD) {
            if (req.cardPaid != null && req.cardPaid) {
                // CARD payment - paid
                payment.setStatus(PaymentStatus.CAPTURED);
                payment.setApprovedAt(LocalDateTime.now());
                order.setStatus(OrderStatus.PAID);
                order.setClosedAt(LocalDateTime.now());
                
                closeOrder(order);
            } else {
                // CARD payment - not paid
                payment.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.PENDING_PAYMENT);
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
        table.setStatus("AVAILABLE"); // Using existing string status for compatibility
        tableRepo.save(table);

        // Stok düş
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                // Create stock movement record
                StockMovement movement = new StockMovement();
                movement.setProductId(item.getProductId());
                movement.setMovementType(StockMovementType.OUT);
                movement.setQuantity(item.getQty());
                movement.setReason("SALE");
                // Note: We can't save the domain entity directly, so we'll use the existing service
                
                // Use existing stock service for compatibility
                stockService.applySaleForOrderItem(item.getProductId(), item.getQty(), order.getId());
            }
        }

        // Siparişi kapat
        order.setStatus(OrderStatus.PAID);
        order.setClosedAt(LocalDateTime.now());
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
                .status(OrderStatus.OPEN)
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .openedAt(LocalDateTime.now())
                .build();
        
        return orderRepo.save(order);
    }

    public OrderEntity getActiveOrderByTable(Long tableId) {
        return orderRepo.findActiveOrderByTableId(tableId)
                .orElse(null);
    }
}
