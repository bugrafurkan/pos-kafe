package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.StockItemEntity;
import com.kafe.infra.entity.StockMovementEntity;
import com.kafe.infra.mapper.StockMapper;
import com.kafe.infra.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StockManualService {

  private final StockItemRepository itemRepo;
  private final StockMovementRepository movementRepo;
  private final ProductRepository productRepo;
  private final StockMapper mapper;

  // -------- Listeleme (stok tablosu) --------
  public Page<StockItemResp> listStock(String query, int page, int size) {
    // Basit implementasyon: product + stock join'i manuel projeksiyon ile.
    // JPA EntityGraph yerine native query ile de yapılabilir; burada Java tarafında zenginleştirelim.
    var pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
    var pageItems = itemRepo.findAll(pageable);

    var content = pageItems.map(si -> {
      var p = productRepo.findById(si.getProductId())
          .orElseThrow(() -> new NoSuchElementException("Product not found: " + si.getProductId()));
      var r = new StockItemResp();
      r.productId = si.getProductId();
      r.sku = p.getSku();
      r.productName = p.getName();
      r.currentQty = si.getCurrentQty();
      r.reorderLevel = si.getReorderLevel();
      r.belowReorder = si.getCurrentQty() != null && si.getReorderLevel() != null
          && si.getCurrentQty().compareTo(si.getReorderLevel()) < 0;
      return r;
    }).getContent();

    // Query filtresi uygulanacaksa burada content'i filtreleyip PageImpl'e sarabilirsiniz.
    return new PageImpl<>(content, pageable, pageItems.getTotalElements());
  }

  // -------- Son hareketler --------
  public Page<StockMovementResp> listMovements(int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    var slice = movementRepo.findAll(pageable).map(mapper::toResp);
    return slice;
  }

  // -------- Manuel hareket oluştur --------
  @Transactional
  public StockMovementResp createMovement(StockMovementCreateReq req) {
    // Ürün var mı?
    var product = productRepo.findById(req.productId)
        .orElseThrow(() -> new NoSuchElementException("Product not found: " + req.productId));

    // Stok item yoksa oluştur
    var item = itemRepo.findByProductId(req.productId)
        .orElseGet(() -> itemRepo.save(StockItemEntity.builder()
            .productId(req.productId).currentQty(BigDecimal.ZERO).reorderLevel(BigDecimal.ZERO).build()));

    // qtyDelta hesabı
    BigDecimal delta = switch (req.reason) {
      case MANUAL_IN -> req.quantity;              // + gir
      case MANUAL_OUT -> req.quantity.negate();    // - çık
      case CORRECTION -> req.quantity;             // düzeltmede pozitif/negatif? — sade: geleni + yaz, UI negatif gönderebilir
    };

    // current_qty update
    itemRepo.addDelta(req.productId, delta);

    // hareket kaydı
    var m = new StockMovementEntity();
    m.setProductId(req.productId);
    m.setQtyDelta(delta);
    m.setReason(req.reason.name());
    m.setRefType("ADJUSTMENT");
    m.setRefId(null);
    m.setOccurredAt(OffsetDateTime.now());
    m.setNote(req.note);
    movementRepo.save(m);

    return mapper.toResp(m);
  }

  // -------- Reorder seviyesi güncelle --------
  @Transactional
  public StockItemResp setReorderLevel(Long productId, BigDecimal newLevel) {
    var item = itemRepo.findByProductId(productId)
        .orElseGet(() -> itemRepo.save(StockItemEntity.builder()
            .productId(productId).currentQty(BigDecimal.ZERO).reorderLevel(BigDecimal.ZERO).build()));
    item.setReorderLevel(newLevel);
    itemRepo.save(item);

    var p = productRepo.findById(productId).orElseThrow();
    var r = new StockItemResp();
    r.productId = productId;
    r.sku = p.getSku();
    r.productName = p.getName();
    r.currentQty = item.getCurrentQty();
    r.reorderLevel = item.getReorderLevel();
    r.belowReorder = r.currentQty.compareTo(r.reorderLevel) < 0;
    return r;
  }
}
