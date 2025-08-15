package com.kafe.infra.service;

import com.kafe.core.domain.OrderStatus;
import com.kafe.core.domain.PaymentStatus;
import com.kafe.core.domain.PaymentType;
import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.infra.entity.OrderEntity;
import com.kafe.infra.entity.PaymentEntity;
import com.kafe.infra.entity.TableEntity;
import com.kafe.infra.repo.OrderRepository;
import com.kafe.infra.repo.PaymentRepository;
import com.kafe.infra.repo.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepo;
    @Mock private PaymentRepository paymentRepo;
    @Mock private TableRepository tableRepo;
    @Mock private StockService stockService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepo, paymentRepo, tableRepo, stockService);
    }

    @Test
    void testCreateOrder() {
        // Given
        Long tableId = 1L;
        TableEntity table = TableEntity.builder()
                .id(tableId)
                .code("T1")
                .name("Table 1")
                .status("AVAILABLE")
                .build();

        when(tableRepo.findById(tableId)).thenReturn(Optional.of(table));
        when(tableRepo.save(any(TableEntity.class))).thenReturn(table);
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        OrderEntity result = orderService.createOrder(tableId);

        // Then
        verify(tableRepo).findById(tableId);
        verify(tableRepo).save(any(TableEntity.class));
        verify(orderRepo).save(any(OrderEntity.class));
        
        assert result.getTableId().equals(tableId);
        assert result.getStatus() == OrderStatus.OPEN;
    }

    @Test
    void testFinalizePayment_Cash() {
        // Given
        Long orderId = 1L;
        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .tableId(1L)
                .status(OrderStatus.OPEN)
                .total(new BigDecimal("100.00"))
                .build();

        OrderPaymentReq req = new OrderPaymentReq();
        req.paymentType = PaymentType.CASH;
        req.cardPaid = null;

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepo.save(any(PaymentEntity.class))).thenReturn(new PaymentEntity());
        when(orderRepo.save(any(OrderEntity.class))).thenReturn(order);

        // When
        orderService.finalizePayment(orderId, req);

        // Then
        verify(paymentRepo).save(any(PaymentEntity.class));
        verify(orderRepo).save(any(OrderEntity.class));
        verify(stockService, never()).applySaleForOrderItem(any(), any(), any());
    }
}
