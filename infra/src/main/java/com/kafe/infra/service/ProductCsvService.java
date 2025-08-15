package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.ProductEntity;
import com.kafe.infra.entity.StockItemEntity;
import com.kafe.infra.entity.CategoryEntity;
import com.kafe.infra.repo.ProductRepository;
import com.kafe.infra.repo.StockItemRepository;
import com.kafe.infra.repo.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ProductCsvService {

    private final ProductRepository productRepo;
    private final StockItemRepository stockRepo;
    private final CategoryRepository categoryRepo;

    public byte[] exportProducts() throws IOException {
        var out = new ByteArrayOutputStream();
        try (var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             var csv = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("sku","name","price","category","stockQty","reorderLevel"))) {
            var products = productRepo.findAll();
            for (var p : products) {
                var stock = stockRepo.findByProductId(p.getId()).orElse(null);
                var category = categoryRepo.findById(p.getCategoryId()).orElse(null);
                csv.printRecord(
                        p.getSku(),
                        p.getName(),
                        p.getListPrice(),
                        category != null ? category.getName() : "",
                        stock != null ? stock.getCurrentQty() : BigDecimal.ZERO,
                        stock != null ? stock.getReorderLevel() : BigDecimal.ZERO
                );
            }
        }
        return out.toByteArray();
    }

    @Transactional
    public CsvImportResult importProducts(InputStream csvStream) throws IOException {
        CsvImportResult result = new CsvImportResult();
        try (var reader = new InputStreamReader(csvStream, StandardCharsets.UTF_8);
             var parser = CSVParser.parse(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            for (var record : parser) {
                result.totalRows++;
                try {
                    String sku = record.get("sku").trim();
                    String name = record.get("name").trim();
                    BigDecimal price = new BigDecimal(record.get("price").trim());
                    String categoryName = record.get("category").trim();
                    BigDecimal stockQty = new BigDecimal(record.get("stockQty").trim());
                    BigDecimal reorderLevel = new BigDecimal(record.get("reorderLevel").trim());

                    // Find or create category
                    CategoryEntity category = categoryRepo.findByName(categoryName).orElse(null);
                    if (category == null && !categoryName.isEmpty()) {
                        category = categoryRepo.save(CategoryEntity.builder()
                                .name(categoryName)
                                .vatRate(BigDecimal.ZERO)
                                .build());
                    }

                    var product = productRepo.findBySku(sku).orElse(null);
                    if (product == null) {
                        product = new ProductEntity();
                        product.setSku(sku);
                        result.created++;
                    } else {
                        result.updated++;
                    }
                    product.setName(name);
                    product.setListPrice(price);
                    product.setCategoryId(category != null ? category.getId() : 1L); // Default to category 1 if no category
                    productRepo.save(product);

                    final Long productId = product.getId();
                    var stock = stockRepo.findByProductId(productId)
                            .orElseGet(() -> {
                                var si = new StockItemEntity();
                                si.setProductId(productId);
                                return si;
                            });
                    stock.setCurrentQty(stockQty);
                    stock.setReorderLevel(reorderLevel);
                    stockRepo.save(stock);

                } catch (Exception e) {
                    result.errors++;
                }
            }
        }
        return result;
    }
}
