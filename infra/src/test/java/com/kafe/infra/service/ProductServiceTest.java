package com.kafe.infra.service;

import com.kafe.core.dto.PagedResp;
import com.kafe.core.dto.ProductCreateReq;
import com.kafe.core.dto.ProductPriceChangeReq;
import com.kafe.core.dto.ProductResp;
import com.kafe.core.dto.ProductUpdateReq;
import com.kafe.infra.entity.ProductEntity;
import com.kafe.infra.entity.ProductPriceHistoryEntity;
import com.kafe.infra.mapper.ProductMapper;
import com.kafe.infra.repo.ProductPriceHistoryRepository;
import com.kafe.infra.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository repo;
    @Mock private ProductPriceHistoryRepository priceRepo;
    @Mock private ProductMapper mapper;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repo, priceRepo, mapper);
    }

    @Test
    void search_returnsPagedRespAndMapsContent() {
        ProductEntity e = ProductEntity.builder().id(10L).name("Latte").build();
        when(repo.search(isNull(), isNull(), eq(Boolean.TRUE), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(e), PageRequest.of(0, 5), 1));
        ProductResp r = new ProductResp(); r.id = 10L; r.name = "Latte";
        when(mapper.toResp(e)).thenReturn(r);

        PagedResp<ProductResp> out = service.search("", null, true, 0, 5);

        assertThat(out.totalElements).isEqualTo(1);
        assertThat(out.content).hasSize(1);
        assertThat(out.content.get(0).name).isEqualTo("Latte");
    }

    @Test
    void get_throwsWhenNotFoundOrDeleted() {
        when(repo.findById(1L)).thenReturn(Optional.of(ProductEntity.builder().id(1L).deletedAt(java.time.OffsetDateTime.now()).build()));
        assertThatThrownBy(() -> service.get(1L)).isInstanceOf(NoSuchElementException.class);

        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(2L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_validatesMinMaxAndSaves() {
        ProductCreateReq req = new ProductCreateReq();
        req.minPrice = new BigDecimal("10.00");
        req.maxPrice = new BigDecimal("20.00");
        ProductEntity entity = new ProductEntity();
        when(mapper.toEntity(req)).thenReturn(entity);
        ProductResp resp = new ProductResp(); resp.id = 5L;
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toResp(entity)).thenReturn(resp);

        ProductResp out = service.create(req);
        assertThat(out.id).isEqualTo(5L);
    }

    @Test
    void create_throwsWhenMinGreaterThanMax() {
        ProductCreateReq req = new ProductCreateReq();
        req.minPrice = new BigDecimal("30.00");
        req.maxPrice = new BigDecimal("20.00");
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_throwsWhenNotFound() {
        ProductUpdateReq req = new ProductUpdateReq();
        when(repo.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(7L, req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void changePrice_throwsOnNegative() {
        ProductPriceChangeReq req = new ProductPriceChangeReq();
        req.newPrice = new BigDecimal("-1.00");
        assertThatThrownBy(() -> service.changePrice(1L, req, 100L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changePrice_writesHistoryAndSavesProduct() {
        ProductEntity e = ProductEntity.builder().id(9L).listPrice(new BigDecimal("15.00")).build();
        when(repo.findById(9L)).thenReturn(Optional.of(e));

        ProductPriceChangeReq req = new ProductPriceChangeReq();
        req.newPrice = new BigDecimal("20.00");
        req.reason = "menu update";

        ProductResp mapped = new ProductResp(); mapped.id = 9L;
        when(mapper.toResp(e)).thenReturn(mapped);

        ProductResp out = service.changePrice(9L, req, 42L);

        assertThat(out.id).isEqualTo(9L);
        // verify history captured
        ArgumentCaptor<ProductPriceHistoryEntity> cap = ArgumentCaptor.forClass(ProductPriceHistoryEntity.class);
        verify(priceRepo).save(cap.capture());
        ProductPriceHistoryEntity h = cap.getValue();
        assertThat(h.getProductId()).isEqualTo(9L);
        assertThat(h.getOldPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(h.getNewPrice()).isEqualTo(new BigDecimal("20.00"));

        verify(repo).save(e);
    }
}
