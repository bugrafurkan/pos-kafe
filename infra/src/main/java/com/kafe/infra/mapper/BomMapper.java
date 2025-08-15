package com.kafe.infra.mapper;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.ProductBomEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BomMapper {
  @Mapping(target="componentProductName", ignore = true)
  BomLineResp toResp(ProductBomEntity e);
}
