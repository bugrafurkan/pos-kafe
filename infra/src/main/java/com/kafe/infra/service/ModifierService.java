package com.kafe.infra.service;

import com.kafe.core.dto.ModifierGroupCreateReq;
import com.kafe.core.dto.ModifierGroupResp;
import com.kafe.core.dto.ModifierGroupUpdateReq;
import com.kafe.core.dto.ModifierOptionCreateReq;
import com.kafe.core.dto.ModifierOptionResp;
import com.kafe.core.dto.ModifierOptionUpdateReq;
import com.kafe.core.dto.ProductModifierLinkResp;
import com.kafe.core.dto.ProductModifiersResp;
import com.kafe.infra.entity.*;
import com.kafe.infra.mapper.ModifierMapper;
import com.kafe.infra.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModifierService {

  private final ModifierGroupRepository groupRepo;
  private final ModifierOptionRepository optionRepo;
  private final ProductModifierLinkRepository linkRepo;
  private final ProductRepository productRepo;
  private final ModifierMapper mapper;

  // ---------- Groups ----------
  public List<ModifierGroupResp> listGroups() {
    return groupRepo.findAll().stream().map(mapper::toResp).toList();
  }
  public ModifierGroupResp getGroup(Long id) {
    var e = groupRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Group not found: "+id));
    return mapper.toResp(e);
  }
  @Transactional
  public ModifierGroupResp createGroup(ModifierGroupCreateReq req) {
    return mapper.toResp(groupRepo.save(mapper.toEntity(req)));
  }
  @Transactional
  public ModifierGroupResp updateGroup(Long id, ModifierGroupUpdateReq req) {
    var e = groupRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Group not found: "+id));
    mapper.updateEntity(e, req);
    return mapper.toResp(groupRepo.save(e));
  }
  @Transactional
  public void deleteGroup(Long id) {
    // Opsiyonları ve linkleri JPA cascade ile silmiyoruz; repo seviyesinde silinir (FK ON DELETE CASCADE var tabloda options için).
    groupRepo.deleteById(id);
  }

  // ---------- Options ----------
  public List<ModifierOptionResp> listOptions(Long groupId) {
    return optionRepo.findByGroupId(groupId).stream().map(mapper::toResp).toList();
  }
  @Transactional
  public ModifierOptionResp createOption(Long groupId, ModifierOptionCreateReq req) {
    var e = mapper.toEntity(req);
    e.setGroupId(groupId);
    return mapper.toResp(optionRepo.save(e));
  }
  @Transactional
  public ModifierOptionResp updateOption(Long optionId, ModifierOptionUpdateReq req) {
    var e = optionRepo.findById(optionId).orElseThrow(() -> new NoSuchElementException("Option not found: "+optionId));
    mapper.updateEntity(e, req);
    return mapper.toResp(optionRepo.save(e));
  }
  @Transactional
  public void deleteOption(Long optionId) { optionRepo.deleteById(optionId); }

  // ---------- Product Links ----------
  @Transactional
  public ProductModifierLinkResp linkProductGroup(Long productId, Long groupId) {
    // Ürün ve grup var mı kontrol
    productRepo.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found: "+productId));
    groupRepo.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found: "+groupId));
    if (!linkRepo.existsByProductIdAndGroupId(productId, groupId)) {
      linkRepo.save(ProductModifierLinkEntity.builder().productId(productId).groupId(groupId).build());
    }
    var out = new ProductModifierLinkResp();
    out.productId = productId; out.groupId = groupId;
    return out;
  }
  @Transactional
  public void unlinkProductGroup(Long productId, Long groupId) {
    linkRepo.deleteByProductIdAndGroupId(productId, groupId);
  }

  // ---------- Product → Groups+Options ----------
  public ProductModifiersResp getProductModifiers(Long productId) {
    productRepo.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found: "+productId));
    var links = linkRepo.findByProductId(productId);
    var groupIds = links.stream().map(ProductModifierLinkEntity::getGroupId).toList();
    var groups = groupRepo.findAllById(groupIds);
    var optionsByGroup = optionRepo.findAll().stream().collect(Collectors.groupingBy(ModifierOptionEntity::getGroupId));

    var resp = new ProductModifiersResp();
    resp.productId = productId;
    resp.groups = groups.stream().map(g -> {
      var gvo = new ProductModifiersResp.ModifierGroupWithOptions();
      gvo.groupId = g.getId(); gvo.groupName = g.getName(); gvo.required = g.isRequired(); gvo.maxSelect = g.getMaxSelect();
      var opts = optionsByGroup.getOrDefault(g.getId(), List.of()).stream().map(mapper::toResp).toList();
      gvo.options = opts;
      return gvo;
    }).toList();
    return resp;
  }
}
