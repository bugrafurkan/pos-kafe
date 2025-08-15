package com.kafe.infra.mapper;

import com.kafe.infra.entity.ProductEntity;
import com.kafe.core.dto.ProductCreateReq;
import com.kafe.core.dto.ProductResp;
import com.kafe.core.dto.ProductUpdateReq;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductEntity toEntity(ProductCreateReq req);
  ProductResp toResp(ProductEntity e);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget ProductEntity e, ProductUpdateReq req);
}
