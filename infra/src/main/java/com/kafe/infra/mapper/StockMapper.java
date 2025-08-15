package com.kafe.infra.mapper;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.StockMovementEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StockMapper {

  @Mapping(target="id", source="id")
  @Mapping(target="productId", source="productId")
  @Mapping(target="qtyDelta", source="qtyDelta")
  @Mapping(target="reason", source="reason")
  @Mapping(target="refType", source="refType")
  @Mapping(target="refId", source="refId")
  @Mapping(target="occurredAt", source="occurredAt")
  @Mapping(target="note", source="note")
  StockMovementResp toResp(StockMovementEntity e);
}
