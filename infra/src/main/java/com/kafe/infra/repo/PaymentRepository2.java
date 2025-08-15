package com.kafe.infra.repo;

import com.kafe.infra.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository2 extends JpaRepository<PaymentEntity, Long> { }
