package com.kafe.infra.repo;

import com.kafe.infra.entity.CafeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeTableRepository extends JpaRepository<CafeTableEntity, Long> { }
