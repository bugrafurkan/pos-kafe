package com.kafe.infra.repo;

import com.kafe.infra.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

  @Autowired ProductRepository repo;

  @Test
  void createAndSearch() {
    ProductEntity e = ProductEntity.builder()
      .sku("LATTE-12OZ").name("Latte 12oz").categoryId(1L)
      .listPrice(new BigDecimal("95.00")).priceIncludesVat(true)
      .unit("pcs").active(true).build();
    repo.save(e);

    var page = repo.search("Latte", null, true, org.springframework.data.domain.PageRequest.of(0,10));
    assertThat(page.getTotalElements()).isGreaterThan(0);
  }
}
