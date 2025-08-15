package com.kafe.infra.repo;

import com.kafe.infra.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
    
    Optional<TableEntity> findByCode(String code);
}
