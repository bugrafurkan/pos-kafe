package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.ProductBomEntity;
import com.kafe.infra.mapper.BomMapper;
import com.kafe.infra.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BomService {

  private final ProductRepository productRepo;
  private final ProductBomRepository bomRepo;
  private final ProductRepository prodRepo;
  private final BomMapper mapper;

  public BomResp getBom(Long productId) {
    assertProduct(productId);
    var lines = bomRepo.findByProductId(productId).stream().map(e -> {
      var r = mapper.toResp(e);
      r.componentProductName = prodRepo.findById(e.getComponentProductId())
          .map(p -> p.getName()).orElse("N/A");
      return r;
    }).toList();
    var out = new BomResp();
    out.productId = productId; out.lines = lines;
    return out;
  }

  @Transactional
  public BomResp upsertBom(Long productId, BomUpsertReq req) {
    assertProduct(productId);
    // sil & ekle (basit ve güvenli yaklaşım)
    bomRepo.deleteByProductId(productId);
    if (req.lines != null) {
      for (var l : req.lines) {
        if (l.componentProductId.equals(productId))
          throw new IllegalArgumentException("BOM cannot reference itself");
        bomRepo.save(ProductBomEntity.builder()
            .productId(productId)
            .componentProductId(l.componentProductId)
            .componentQty(l.componentQty)
            .build());
      }
    }
    return getBom(productId);
  }

  private void assertProduct(Long id) {
    productRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
  }
}
