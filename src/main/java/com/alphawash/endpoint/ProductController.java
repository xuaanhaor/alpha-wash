package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.ProductDto;
import com.alphawash.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_PRODUCTS)
@RequiredArgsConstructor
@Tag(name = "Products", description = "Manage retail products")
public class ProductController {

    private final ProductService service;

    @Operation(summary = "Get all products")
    @GetMapping(ROOT)
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get active products")
    @GetMapping("/active")
    public ResponseEntity<List<ProductDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @Operation(summary = "Get product by code")
    @GetMapping(CODE_PATH_PARAMETER)
    public ResponseEntity<ProductDto> getByCode(@PathVariable String code) {
        ProductDto dto = service.getByCode(code);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get product by barcode")
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductDto> getByBarcode(@PathVariable String barcode) {
        ProductDto dto = service.getByBarcode(barcode);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get products by category")
    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<ProductDto>> getByCategoryCode(@PathVariable String categoryCode) {
        return ResponseEntity.ok(service.getByCategoryCode(categoryCode));
    }

    @Operation(summary = "Create a product")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Update a product")
    @PatchMapping(UPDATE_ENDPOINT + "/code/{code}")
    public ResponseEntity<ProductDto> update(@PathVariable String code, @RequestBody ProductDto request) {
        ProductDto updated = service.update(code, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping(DELETE_ENDPOINT + "/code/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
