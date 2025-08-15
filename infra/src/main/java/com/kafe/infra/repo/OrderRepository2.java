package com.kafe.infra.repo;

import com.kafe.infra.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository2 extends JpaRepository<OrderEntity, Long> { }
