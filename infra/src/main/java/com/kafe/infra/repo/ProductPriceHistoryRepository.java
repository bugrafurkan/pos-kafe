package com.kafe.infra.repo;

import com.kafe.infra.entity.ProductPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistoryEntity, Long> { }
