package com.kafe.api.controller;

import com.kafe.core.dto.ModifierGroupCreateReq;
import com.kafe.core.dto.ModifierGroupResp;
import com.kafe.core.dto.ModifierGroupUpdateReq;
import com.kafe.core.dto.ModifierOptionCreateReq;
import com.kafe.core.dto.ModifierOptionResp;
import com.kafe.core.dto.ModifierOptionUpdateReq;
import com.kafe.core.dto.ProductModifierLinkResp;
import com.kafe.core.dto.ProductModifiersResp;
import com.kafe.infra.service.ModifierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ModifierController {

  private final ModifierService svc;

  // ------- Groups -------
  @GetMapping("/modifier-groups")
  public List<ModifierGroupResp> listGroups() { return svc.listGroups(); }

  @GetMapping("/modifier-groups/{groupId}")
  public ModifierGroupResp getGroup(@PathVariable Long groupId) { return svc.getGroup(groupId); }

  @PostMapping("/modifier-groups")
  public ModifierGroupResp createGroup(@Valid @RequestBody ModifierGroupCreateReq req) { return svc.createGroup(req); }

  @PutMapping("/modifier-groups/{groupId}")
  public ModifierGroupResp updateGroup(@PathVariable Long groupId, @Valid @RequestBody ModifierGroupUpdateReq req) {
    return svc.updateGroup(groupId, req);
  }

  @DeleteMapping("/modifier-groups/{groupId}")
  public void deleteGroup(@PathVariable Long groupId) { svc.deleteGroup(groupId); }

  // ------- Options -------
  @GetMapping("/modifier-groups/{groupId}/options")
  public List<ModifierOptionResp> listOptions(@PathVariable Long groupId) { return svc.listOptions(groupId); }

  @PostMapping("/modifier-groups/{groupId}/options")
  public ModifierOptionResp createOption(@PathVariable Long groupId, @Valid @RequestBody ModifierOptionCreateReq req) {
    return svc.createOption(groupId, req);
  }

  @PutMapping("/modifier-options/{optionId}")
  public ModifierOptionResp updateOption(@PathVariable Long optionId, @Valid @RequestBody ModifierOptionUpdateReq req) {
    return svc.updateOption(optionId, req);
  }

  @DeleteMapping("/modifier-options/{optionId}")
  public void deleteOption(@PathVariable Long optionId) { svc.deleteOption(optionId); }

  // ------- Product Links -------
  @PostMapping("/products/{productId}/modifiers/{groupId}")
  public ProductModifierLinkResp link(@PathVariable Long productId, @PathVariable Long groupId) {
    return svc.linkProductGroup(productId, groupId);
  }

  @DeleteMapping("/products/{productId}/modifiers/{groupId}")
  public void unlink(@PathVariable Long productId, @PathVariable Long groupId) {
    svc.unlinkProductGroup(productId, groupId);
  }

  @GetMapping("/products/{productId}/modifiers")
  public ProductModifiersResp getProductModifiers(@PathVariable Long productId) {
    return svc.getProductModifiers(productId);
  }
}
