package com.kafe.infra.repo;

import com.kafe.infra.entity.StockMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

  @Modifying
  @Query(value = """
      INSERT INTO stock_movement(product_id, qty_delta, reason, ref_type, ref_id, note)
      VALUES (:productId, :qtyDelta, :reason, :refType, :refId, :note)
      """, nativeQuery = true)
  void insert(Long productId, java.math.BigDecimal qtyDelta, String reason, String refType, Long refId, String note);
}
