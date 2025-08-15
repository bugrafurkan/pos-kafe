package com.kafe.api.controller;

import com.kafe.core.dto.*;
import com.kafe.infra.service.BomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/bom")
public class BomController {

  private final BomService svc;

  @GetMapping
  public BomResp get(@PathVariable Long productId) { return svc.getBom(productId); }

  @PutMapping
  public BomResp upsert(@PathVariable Long productId, @Valid @RequestBody BomUpsertReq req) {
    return svc.upsertBom(productId, req);
  }
}
