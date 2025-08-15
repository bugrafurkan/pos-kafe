package com.kafe.infra.repo;

import com.kafe.infra.entity.StockItemEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface StockItemRepository extends JpaRepository<StockItemEntity, Long> {

  Optional<StockItemEntity> findByProductId(Long productId);

  @Modifying
  @Query("""
     update StockItemEntity s set s.currentQty = s.currentQty + :delta
     where s.productId = :productId
  """)
  int addDelta(@Param("productId") Long productId, @Param("delta") java.math.BigDecimal delta);
}
