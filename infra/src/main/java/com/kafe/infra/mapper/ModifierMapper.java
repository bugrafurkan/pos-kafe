package com.kafe.infra.mapper;

import com.kafe.core.dto.ModifierGroupCreateReq;
import com.kafe.core.dto.ModifierGroupResp;
import com.kafe.core.dto.ModifierGroupUpdateReq;
import com.kafe.core.dto.ModifierOptionCreateReq;
import com.kafe.core.dto.ModifierOptionResp;
import com.kafe.core.dto.ModifierOptionUpdateReq;
import com.kafe.infra.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ModifierMapper {
  // Group
  ModifierGroupEntity toEntity(ModifierGroupCreateReq req);
  ModifierGroupResp toResp(ModifierGroupEntity e);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget ModifierGroupEntity e, ModifierGroupUpdateReq req);

  // Option
  ModifierOptionEntity toEntity(ModifierOptionCreateReq req);
  ModifierOptionResp toResp(ModifierOptionEntity e);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget ModifierOptionEntity e, ModifierOptionUpdateReq req);
}
