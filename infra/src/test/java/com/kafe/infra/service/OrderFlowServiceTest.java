package com.kafe.infra.service;

import com.kafe.core.dto.AddOrderItemReq;
import com.kafe.core.dto.CreateOrderReq;
import com.kafe.core.dto.FinalizePaymentReq;
import com.kafe.infra.entity.*;
import com.kafe.infra.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFlowServiceTest {

    @Mock private OrderRepository2 orderRepo;
    @Mock private OrderItemRepository2 itemRepo;
    @Mock private PaymentRepository2 payRepo;
    @Mock private CafeTableRepository tableRepo;
    @Mock private ProductRepository productRepo;
    @Mock private ProductCategoryRepository categoryRepo;
    @Mock private StockService stockService;

    private OrderFlowService service;

    @BeforeEach
    void setup() {
        service = new OrderFlowService(orderRepo, itemRepo, payRepo, tableRepo, productRepo, categoryRepo, stockService);
    }

    @Test
    void createOrder_marksTableOccupied_and_persistsOrder() {
        CreateOrderReq req = new CreateOrderReq();
        req.tableId = 1L;
        CafeTableEntity t = CafeTableEntity.builder().id(1L).status("AVAILABLE").build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(t));
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });

        var summary = service.createOrder(req);

        verify(tableRepo).save(argThat(tab -> "OCCUPIED".equals(tab.getStatus())));
        verify(orderRepo).save(any(OrderEntity.class));
        assertThat(summary.orderId).isEqualTo(99L);
        assertThat(summary.status).isEqualTo("OPEN");
    }

    @Test
    void addItem_calculatesVatAndTotals_priceIncludesVat_true() {
        OrderEntity order = OrderEntity.builder().id(5L).status("OPEN").discountRate(BigDecimal.ZERO).build();
        when(orderRepo.findById(5L)).thenReturn(Optional.of(order));
        ProductEntity p = ProductEntity.builder().id(7L).listPrice(new BigDecimal("110.00")).priceIncludesVat(true).categoryId(3L).build();
        when(productRepo.findById(7L)).thenReturn(Optional.of(p));
        ProductCategoryEntity cat = ProductCategoryEntity.builder().id(3L).vatRate(new BigDecimal("10")).build();
        when(categoryRepo.findById(3L)).thenReturn(Optional.of(cat));
        // When saving the line, ensure subsequent findByOrderId returns that line
        when(itemRepo.save(any(OrderItemEntity.class))).thenAnswer(inv -> {
            OrderItemEntity line = inv.getArgument(0);
            when(itemRepo.findByOrderId(5L)).thenReturn(List.of(line));
            return line;
        });

        AddOrderItemReq req = new AddOrderItemReq();
        req.productId = 7L;
        req.qty = new BigDecimal("2");

        var sum = service.addItem(5L, req);

        verify(itemRepo).save(any(OrderItemEntity.class));
        verify(orderRepo, atLeastOnce()).save(order);
        assertThat(sum.items).hasSize(1);
        assertThat(sum.grandTotal).isEqualTo(new BigDecimal("220.00"));
    }

    @Test
    void goPayment_setsStatusPending() {
        OrderEntity order = OrderEntity.builder().id(5L).status("OPEN").build();
        when(orderRepo.findById(5L)).thenReturn(Optional.of(order));
        when(itemRepo.findByOrderId(5L)).thenReturn(List.of());

        var sum = service.goPayment(5L);

        assertThat(sum.status).isEqualTo("PENDING_PAYMENT");
        verify(orderRepo).save(order);
    }

    @Test
    void finalizePayment_cash_capturesAndCloses() {
        OrderEntity order = OrderEntity.builder().id(5L).tableId(1L).status("PENDING_PAYMENT").grandTotal(new BigDecimal("50.00")).build();
        when(orderRepo.findById(5L)).thenReturn(Optional.of(order));
        when(itemRepo.findByOrderId(5L)).thenReturn(List.of());
        when(tableRepo.findById(1L)).thenReturn(Optional.of(CafeTableEntity.builder().id(1L).status("OCCUPIED").build()));

        FinalizePaymentReq req = new FinalizePaymentReq();
        req.paymentType = "CASH";

        var sum = service.finalizePayment(5L, req);

        verify(payRepo).save(any(PaymentEntity.class));
        assertThat(sum.status).isEqualTo("PAID");
        verify(orderRepo, atLeastOnce()).save(any(OrderEntity.class));
    }

    @Test
    void finalizePayment_card_notPaid_setsFailedKeepsPending() {
        OrderEntity order = OrderEntity.builder().id(5L).tableId(1L).status("PENDING_PAYMENT").grandTotal(new BigDecimal("50.00")).build();
        when(orderRepo.findById(5L)).thenReturn(Optional.of(order));
        when(itemRepo.findByOrderId(5L)).thenReturn(List.of());

        FinalizePaymentReq req = new FinalizePaymentReq();
        req.paymentType = "CARD";
        req.cardPaid = false;

        var sum = service.finalizePayment(5L, req);

        verify(payRepo).save(any(PaymentEntity.class));
        assertThat(sum.status).isEqualTo("PENDING_PAYMENT");
    }

    @Test
    void createOrder_throwsWhenTableMissing() {
        CreateOrderReq req = new CreateOrderReq(); req.tableId = 77L;
        when(tableRepo.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createOrder(req)).isInstanceOf(NoSuchElementException.class);
    }
}
