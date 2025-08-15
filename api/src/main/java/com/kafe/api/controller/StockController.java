package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.StockManualService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockController {

  private final StockManualService svc;

  // Stok listesi (sayfalı)
  @GetMapping
  public PagedResp<StockItemResp> list(@RequestParam(required=false) String query,
                                       @RequestParam(defaultValue="0") int page,
                                       @RequestParam(defaultValue="20") int size) {
    Page<StockItemResp> p = svc.listStock(query, page, size);
    PagedResp<StockItemResp> out = new PagedResp<>();
    out.content = p.getContent();
    out.page = p.getNumber();
    out.size = p.getSize();
    out.totalElements = p.getTotalElements();
    out.totalPages = p.getTotalPages();
    return out;
  }

  // Son hareketler
  @GetMapping("/movements")
  public PagedResp<StockMovementResp> movements(@RequestParam(defaultValue="0") int page,
                                                @RequestParam(defaultValue="20") int size) {
    var p = svc.listMovements(page, size);
    PagedResp<StockMovementResp> out = new PagedResp<>();
    out.content = p.getContent();
    out.page = p.getNumber();
    out.size = p.getSize();
    out.totalElements = p.getTotalElements();
    out.totalPages = p.getTotalPages();
    return out;
  }

  // Manuel hareket
  @PostMapping("/movements")
  public StockMovementResp create(@Valid @RequestBody StockMovementCreateReq req) {
    return svc.createMovement(req);
  }

  // Reorder level güncelle
  @PutMapping("/reorder-level/{productId}")
  public StockItemResp setReorderLevel(@PathVariable Long productId,
                                       @RequestParam BigDecimal newLevel) {
    return svc.setReorderLevel(productId, newLevel);
  }
}
