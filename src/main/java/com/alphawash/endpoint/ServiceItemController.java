package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_SERVICES;

import com.alphawash.constant.ServiceCategory;
import com.alphawash.request.ServiceItemRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.ServiceItemResponse;
import com.alphawash.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_SERVICES)
@RequiredArgsConstructor
@Tag(name = "Services", description = "Quản lý bảng giá dịch vụ")
public class ServiceItemController {

    private final CatalogService catalogService;

    @Operation(summary = "Danh sách dịch vụ (filter theo category, active)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceItemResponse>>> getAll(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(required = false, defaultValue = "true") boolean active) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getAll(category, active)));
    }

    @Operation(summary = "Danh sách categories")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ServiceCategory>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getCategories()));
    }

    @Operation(summary = "Danh sách dịch vụ có thể tặng kèm")
    @GetMapping("/bonus")
    public ResponseEntity<ApiResponse<List<ServiceItemResponse>>> getBonusServices() {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getBonusServices()));
    }

    @Operation(summary = "Chi tiết dịch vụ theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceItemResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getById(id)));
    }

    @Operation(summary = "Thêm dịch vụ mới (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceItemResponse>> create(@RequestBody ServiceItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo dịch vụ thành công", catalogService.create(request)));
    }

    @Operation(summary = "Cập nhật dịch vụ (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceItemResponse>> update(
            @PathVariable UUID id, @RequestBody ServiceItemRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật dịch vụ thành công", catalogService.update(id, request)));
    }

    @Operation(summary = "Xóa / ẩn dịch vụ (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        catalogService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Đã xóa dịch vụ", null));
    }
}
