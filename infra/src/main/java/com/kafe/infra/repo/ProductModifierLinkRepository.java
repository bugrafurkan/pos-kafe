package com.kafe.infra.repo;

import com.kafe.infra.entity.ProductModifierLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductModifierLinkRepository extends JpaRepository<ProductModifierLinkEntity, ProductModifierLinkEntity.Pk> {
  List<ProductModifierLinkEntity> findByProductId(Long productId);
  boolean existsByProductIdAndGroupId(Long productId, Long groupId);
  void deleteByProductIdAndGroupId(Long productId, Long groupId);
}
