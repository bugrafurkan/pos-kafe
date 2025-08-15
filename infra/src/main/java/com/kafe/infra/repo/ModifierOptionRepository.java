package com.kafe.infra.repo;

import com.kafe.infra.entity.ModifierOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModifierOptionRepository extends JpaRepository<ModifierOptionEntity, Long> {
  List<ModifierOptionEntity> findByGroupId(Long groupId);
}
