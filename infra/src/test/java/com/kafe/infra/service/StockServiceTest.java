package com.kafe.infra.service;

import com.kafe.infra.entity.ProductBomEntity;
import com.kafe.infra.repo.ProductBomRepository;
import com.kafe.infra.repo.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock private StockMovementRepository movementRepo;
    @Mock private ProductBomRepository bomRepo;

    @Test
    void applySaleForOrderItem_plainProduct_writesSingleNegativeMovement() {
        StockService service = new StockService(movementRepo, bomRepo);
        when(bomRepo.findByProductId(100L)).thenReturn(List.of());

        service.applySaleForOrderItem(100L, new BigDecimal("2"), 500L);

        verify(movementRepo).insert(eq(100L), eq(new BigDecimal("-2")), eq("SALE"), eq("ORDER"), eq(500L), anyString());
    }

    @Test
    void applySaleForOrderItem_bomProduct_writesComponentsNegativeMovements() {
        StockService service = new StockService(movementRepo, bomRepo);
        ProductBomEntity c1 = ProductBomEntity.builder().productId(200L).componentProductId(10L).componentQty(new BigDecimal("0.50")).build();
        ProductBomEntity c2 = ProductBomEntity.builder().productId(200L).componentProductId(11L).componentQty(new BigDecimal("0.25")).build();
        when(bomRepo.findByProductId(200L)).thenReturn(List.of(c1, c2));

        service.applySaleForOrderItem(200L, new BigDecimal("4"), 600L);

        verify(movementRepo).insert(eq(10L), eq(new BigDecimal("-2.00")), eq("SALE"), eq("ORDER"), eq(600L), anyString());
        verify(movementRepo).insert(eq(11L), eq(new BigDecimal("-1.00")), eq("SALE"), eq("ORDER"), eq(600L), anyString());
    }
}
