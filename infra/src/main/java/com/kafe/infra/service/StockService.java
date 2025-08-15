package com.kafe.infra.service;

import com.kafe.infra.entity.ProductBomEntity;
import com.kafe.infra.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

  private final StockMovementRepository movementRepo;
  private final ProductBomRepository bomRepo;

  @Value("${stock.allowNegative:true}")
  boolean allowNegative;

  // Bu metodu Order PAID olduğunda çağıracağız
  @Transactional
  public void applySaleForOrderItem(Long productId, BigDecimal qty, Long orderId) {
    List<ProductBomEntity> bom = bomRepo.findByProductId(productId);
    if (bom.isEmpty()) {
      writeMovement(productId, qty.negate(), "SALE", "ORDER", orderId);
    } else {
      for (var line : bom) {
        var delta = line.getComponentQty().multiply(qty).negate(); // eksi
        writeMovement(line.getComponentProductId(), delta, "SALE", "ORDER", orderId);
      }
    }
  }

  private void writeMovement(Long productId, BigDecimal qtyDelta, String reason, String refType, Long refId) {
    // burada allowNegative kontrolü ileride stock_item.current_qty ile yapılabilir.
    movementRepo.insert(productId, qtyDelta, reason, refType, refId, "auto sale");
  }
}
