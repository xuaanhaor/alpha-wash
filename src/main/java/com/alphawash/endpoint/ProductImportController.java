package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.ProductImportHistoryDto;
import com.alphawash.dto.ProductImportPreviewDto;
import com.alphawash.dto.ProductImportResultDto;
import com.alphawash.service.ProductImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(API_PRODUCTS + "/import")
@RequiredArgsConstructor
@Tag(name = "Product Import", description = "Bulk import products from Excel")
public class ProductImportController {

    private final ProductImportService importService;

    @Operation(summary = "Download import template")
    @GetMapping("/template")
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        ByteArrayResource resource = importService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @Operation(summary = "Upload and validate Excel file")
    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImportPreviewDto> validate(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ProductImportPreviewDto preview = importService.parseAndValidate(inputStream, file.getOriginalFilename());
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Execute import after validation")
    @PostMapping
    public ResponseEntity<ProductImportResultDto> importProducts(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "CREATE_ONLY") String importMode) {
        try {
            InputStream inputStream = file.getInputStream();
            ProductImportPreviewDto preview = importService.parseAndValidate(inputStream, file.getOriginalFilename());
            ProductImportResultDto result = importService.importProducts(preview, importMode, file.getOriginalFilename());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Download error report")
    @PostMapping(value = "/error-report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> errorReport(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ProductImportPreviewDto preview = importService.parseAndValidate(inputStream, file.getOriginalFilename());
            ByteArrayResource resource = importService.generateErrorReport(preview);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_errors.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get import history")
    @GetMapping("/history")
    public ResponseEntity<List<ProductImportHistoryDto>> getHistory() {
        return ResponseEntity.ok(importService.getImportHistory());
    }

    @Operation(summary = "Get import history by ID")
    @GetMapping("/history/{id}")
    public ResponseEntity<ProductImportHistoryDto> getHistoryById(@PathVariable Long id) {
        ProductImportHistoryDto dto = importService.getImportHistoryById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}
