package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.ProductCategoryDto;
import com.alphawash.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_PRODUCT_CATEGORIES)
@RequiredArgsConstructor
@Tag(name = "Product Categories", description = "Manage product categories")
public class ProductCategoryController {

    private final ProductCategoryService service;

    @Operation(summary = "Get all product categories")
    @GetMapping(ROOT)
    public ResponseEntity<List<ProductCategoryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get active product categories")
    @GetMapping("/active")
    public ResponseEntity<List<ProductCategoryDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @Operation(summary = "Get product category by code")
    @GetMapping(CODE_PATH_PARAMETER)
    public ResponseEntity<ProductCategoryDto> getByCode(@PathVariable String code) {
        ProductCategoryDto dto = service.getByCode(code);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a product category")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ProductCategoryDto> create(@RequestBody ProductCategoryDto request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Update a product category")
    @PatchMapping(UPDATE_ENDPOINT + "/code/{code}")
    public ResponseEntity<ProductCategoryDto> update(@PathVariable String code, @RequestBody ProductCategoryDto request) {
        ProductCategoryDto updated = service.update(code, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a product category")
    @DeleteMapping(DELETE_ENDPOINT + "/code/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
