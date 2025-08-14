package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;

  @GetMapping
  public PagedResp<ProductResp> list(
      @RequestParam(required=false) String query,
      @RequestParam(required=false) Long categoryId,
      @RequestParam(required=false) Boolean active,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size) {
    return service.search(query, categoryId, active, page, size);
  }

  @GetMapping("/{id}")
  public ProductResp get(@PathVariable Long id) { return service.get(id); }

  @PostMapping
  public ProductResp create(@Valid @RequestBody ProductCreateReq req) { return service.create(req); }

  @PutMapping("/{id}")
  public ProductResp update(@PathVariable Long id, @Valid @RequestBody ProductUpdateReq req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) { service.softDelete(id); }

  @PostMapping("/{id}/price")
  public ProductResp changePrice(@PathVariable Long id, @Valid @RequestBody ProductPriceChangeReq req) {
    // Auth henüz yoksa null veriyoruz; Security gelince currentUserId doldurulacak.
    return service.changePrice(id, req, null);
  }
}
