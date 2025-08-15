package com.kafe.infra.repo;

import com.kafe.core.domain.OrderStatus;
import com.kafe.infra.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByTableIdAndStatus(Long tableId, OrderStatus status);
    
    Optional<OrderEntity> findByTableIdAndStatusIn(Long tableId, List<OrderStatus> statuses);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.tableId = :tableId AND o.status IN ('OPEN', 'PENDING_PAYMENT')")
    Optional<OrderEntity> findActiveOrderByTableId(@Param("tableId") Long tableId);
    
    List<OrderEntity> findByStatus(OrderStatus status);
}
