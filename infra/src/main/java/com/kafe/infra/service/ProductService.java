package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.*;
import com.kafe.infra.mapper.ProductMapper;
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
public class ProductService {

  private final ProductRepository repo;
  private final ProductPriceHistoryRepository priceRepo;
  private final ProductMapper mapper;

  public PagedResp<ProductResp> search(String q, Long categoryId, Boolean active, int page, int size) {
    Page<ProductEntity> p = repo.search(emptyToNull(q), categoryId, active, PageRequest.of(page, size, Sort.by("id").descending()));
    PagedResp<ProductResp> out = new PagedResp<>();
    out.content = p.map(mapper::toResp).getContent();
    out.page = p.getNumber();
    out.size = p.getSize();
    out.totalElements = p.getTotalElements();
    out.totalPages = p.getTotalPages();
    return out;
  }

  public ProductResp get(Long id) {
    ProductEntity e = repo.findById(id).filter(x -> x.getDeletedAt()==null)
      .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    return mapper.toResp(e);
  }

  @Transactional
  public ProductResp create(ProductCreateReq req) {
    validateMinMax(req.minPrice, req.maxPrice);
    ProductEntity e = mapper.toEntity(req);
    return mapper.toResp(repo.save(e));
  }

  @Transactional
  public ProductResp update(Long id, ProductUpdateReq req) {
    validateMinMax(req.minPrice, req.maxPrice);
    ProductEntity e = repo.findById(id).filter(x -> x.getDeletedAt()==null)
      .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    mapper.updateEntity(e, req);
    return mapper.toResp(repo.save(e));
  }

  @Transactional
  public void softDelete(Long id) {
    ProductEntity e = repo.findById(id).filter(x -> x.getDeletedAt()==null)
      .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    e.setActive(false);
    e.setDeletedAt(OffsetDateTime.now());
    repo.save(e);
  }

  @Transactional
  public ProductResp changePrice(Long id, ProductPriceChangeReq req, Long currentUserId) {
    if (req.newPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("newPrice must be >= 0");
    }
    ProductEntity e = repo.findById(id).filter(x -> x.getDeletedAt()==null)
      .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    BigDecimal old = e.getListPrice();
    e.setListPrice(req.newPrice);

    ProductPriceHistoryEntity h = ProductPriceHistoryEntity.builder()
      .productId(e.getId())
      .oldPrice(old)
      .newPrice(req.newPrice)
      .changedBy(currentUserId) // şimdilik null olabilir
      .reason(req.reason)
      .changedAt(OffsetDateTime.now())
      .build();

    priceRepo.save(h);
    repo.save(e);
    return mapper.toResp(e);
  }

  private static void validateMinMax(BigDecimal min, BigDecimal max) {
    if (min != null && max != null && min.compareTo(max) > 0) {
      throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
    }
  }

  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
