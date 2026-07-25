package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_SERVICE_CATEGORIES;

import com.alphawash.request.ServiceCategoryRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.ServiceCategoryResponse;
import com.alphawash.service.ServiceCategoryService;
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
@RequestMapping(API_SERVICE_CATEGORIES)
@RequiredArgsConstructor
@Tag(name = "Service Categories", description = "Quản lý danh mục dịch vụ")
public class ServiceCategoryController {

    private final ServiceCategoryService service;

    @Operation(summary = "Danh sách danh mục dịch vụ")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> getAll(
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(active)));
    }

    @Operation(summary = "Chi tiết danh mục theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @Operation(summary = "Tạo danh mục mới (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> create(@RequestBody ServiceCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo danh mục thành công", service.create(request)));
    }

    @Operation(summary = "Cập nhật danh mục (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> update(
            @PathVariable UUID id, @RequestBody ServiceCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", service.update(id, request)));
    }

    @Operation(summary = "Xóa danh mục (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Đã xóa danh mục", null));
    }
}
