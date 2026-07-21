package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_QUOTES;

import com.alphawash.constant.QuoteStatus;
import com.alphawash.request.QuoteRequest;
import com.alphawash.request.QuoteStatusRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.PageResponse;
import com.alphawash.response.QuoteResponse;
import com.alphawash.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(API_QUOTES)
@RequiredArgsConstructor
@Tag(name = "Quotes", description = "Quản lý báo giá")
public class QuoteController {

    private final QuoteService quoteService;

    @Operation(summary = "Danh sách báo giá (paginated, search, filter status)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<QuoteResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) QuoteStatus status) {
        return ResponseEntity.ok(ApiResponse.success(quoteService.list(search, status, page, size)));
    }

    @Operation(summary = "Chi tiết báo giá + items")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(quoteService.getById(id)));
    }

    @Operation(summary = "Tạo báo giá mới")
    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> create(@RequestBody QuoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo báo giá thành công", quoteService.create(request)));
    }

    @Operation(summary = "Cập nhật báo giá")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> update(
            @PathVariable UUID id, @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật báo giá thành công", quoteService.update(id, request)));
    }

    @Operation(summary = "Xóa báo giá (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        quoteService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Đã xóa báo giá", null));
    }

    @Operation(summary = "Đổi trạng thái báo giá (SENT, ACCEPTED, REJECTED)")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<QuoteResponse>> updateStatus(
            @PathVariable UUID id, @RequestBody QuoteStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật trạng thái thành công", quoteService.updateStatus(id, request)));
    }
}
