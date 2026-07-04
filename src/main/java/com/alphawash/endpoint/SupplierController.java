package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.dto.SupplierDto;
import com.alphawash.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_SUPPLIERS)
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Manage product suppliers")
public class SupplierController {

    private final SupplierService service;

    @Operation(summary = "Get all suppliers")
    @GetMapping(ROOT)
    public ResponseEntity<List<SupplierDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get active suppliers")
    @GetMapping("/active")
    public ResponseEntity<List<SupplierDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @Operation(summary = "Get supplier by code")
    @GetMapping(CODE_PATH_PARAMETER)
    public ResponseEntity<SupplierDto> getByCode(@PathVariable String code) {
        SupplierDto dto = service.getByCode(code);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a supplier")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<SupplierDto> create(@RequestBody SupplierDto request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Update a supplier")
    @PatchMapping(UPDATE_ENDPOINT + "/code/{code}")
    public ResponseEntity<SupplierDto> update(@PathVariable String code, @RequestBody SupplierDto request) {
        SupplierDto updated = service.update(code, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a supplier")
    @DeleteMapping(DELETE_ENDPOINT + "/code/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
