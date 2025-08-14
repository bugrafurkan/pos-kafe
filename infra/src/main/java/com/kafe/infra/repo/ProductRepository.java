package com.kafe.infra.repo;

import com.kafe.infra.entity.ProductEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  @Query("""
    SELECT p FROM ProductEntity p
    WHERE (p.deletedAt IS NULL)
      AND (:active IS NULL OR p.active = :active)
      AND (
            :q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(COALESCE(p.barcode,'')) LIKE LOWER(CONCAT('%', :q, '%'))
      )
      AND (:categoryId IS NULL OR p.categoryId = :categoryId)
    """)
  Page<ProductEntity> search(@Param("q") String q,
                             @Param("categoryId") Long categoryId,
                             @Param("active") Boolean active,
                             Pageable pageable);

  boolean existsByBarcode(String barcode);
}
