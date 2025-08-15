package com.kafe.infra.repo;

import com.kafe.infra.entity.ProductBomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductBomRepository extends JpaRepository<ProductBomEntity, Long> {
  List<ProductBomEntity> findByProductId(Long productId);
  void deleteByProductId(Long productId);
}
