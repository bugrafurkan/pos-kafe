package com.kafe.infra.service;


import com.kafe.core.domain.PaymentType;
import com.kafe.core.dto.OrderPaymentReq;
import com.kafe.infra.entity.OrderEntity;
import com.kafe.infra.entity.PaymentEntity;
import com.kafe.infra.entity.TableEntity;
import com.kafe.infra.repo.OrderRepository;
import com.kafe.infra.repo.PaymentRepository;
import com.kafe.infra.repo.TableRepository;
import com.kafe.infra.repo.OrderItemRepository2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepo;
    @Mock private PaymentRepository paymentRepo;
    @Mock private TableRepository tableRepo;
    @Mock private OrderItemRepository2 itemRepo;
    @Mock private StockService stockService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepo, paymentRepo, tableRepo, itemRepo, stockService);
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
        assert "OPEN".equals(result.getStatus());
    }

    @Test
    void testFinalizePayment_Cash() {
        // Given
        Long orderId = 1L;
        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .tableId(1L)
                .status("OPEN")
                .grandTotal(new BigDecimal("100.00"))
                .build();

        OrderPaymentReq req = new OrderPaymentReq();
        req.paymentType = PaymentType.CASH;
        req.cardPaid = null;

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepo.save(any(PaymentEntity.class))).thenReturn(new PaymentEntity());
        when(orderRepo.save(any(OrderEntity.class))).thenReturn(order);
        
        // Mock table repository for closeOrder method
        TableEntity table = TableEntity.builder()
                .id(1L)
                .code("T1")
                .name("Table 1")
                .status("OCCUPIED")
                .build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(tableRepo.save(any(TableEntity.class))).thenReturn(table);

        // When
        orderService.finalizePayment(orderId, req);

        // Then
        verify(paymentRepo).save(any(PaymentEntity.class));
        verify(orderRepo, times(2)).save(any(OrderEntity.class)); // Called once in finalizePayment and once in closeOrder
        verify(stockService, never()).applySaleForOrderItem(any(), any(), any());
    }
}
