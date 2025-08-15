package com.kafe.infra.repo;

import com.kafe.core.domain.PaymentStatus;
import com.kafe.infra.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    
    List<PaymentEntity> findByOrderId(Long orderId);
    
    Optional<PaymentEntity> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
