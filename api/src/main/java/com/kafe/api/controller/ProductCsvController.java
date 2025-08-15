package com.kafe.api.controller;

import com.kafe.core.dto.CsvImportResult;
import com.kafe.infra.service.ProductCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/csv")
public class ProductCsvController {

    private final ProductCsvService csvService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() throws IOException {
        byte[] data = csvService.exportProducts();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @PostMapping("/import")
    public CsvImportResult importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        return csvService.importProducts(file.getInputStream());
    }
}
