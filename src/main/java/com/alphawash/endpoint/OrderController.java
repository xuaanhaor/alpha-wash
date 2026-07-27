package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.request.BulkPaymentRequest;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.PageResponse;
import com.alphawash.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.API_ORDERS)
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @GetMapping(Constant.ROOT)
    @Operation(
            summary = "Get all orders with pagination",
            description = "Returns a paginated list of orders including customer, vehicle, service, and employee details")
    public ResponseEntity<ApiResponse<PageResponse<OrderFullDto>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersPaged(page, size)));
    }

    @GetMapping(Constant.CODE_PATH_PARAMETER)
    public ResponseEntity<OrderFullDto> getOrderByCode(@PathVariable String code) {
        OrderFullDto dto = orderService.getOrderByCode(code);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping(Constant.ID_PATH_PARAMETER)
    public ResponseEntity<OrderFullDto> getOrderById(@PathVariable UUID id) {
        OrderFullDto dto = orderService.getOrderById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping(Constant.API_CREATE_ORDER)
    public ResponseEntity<UUID> createOrder(@RequestBody OrderCreateRequest request) {
        UUID id = orderService.createOrder(request);
        if (id == null || id.toString().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(id);
    }

    @PatchMapping(Constant.UPDATE_ENDPOINT)
    public ResponseEntity<Void> updateOrder(@RequestBody OrderUpdateRequest request) {
        orderService.updateOrder(request);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/bulk-payment")
    @Operation(summary = "Bulk update payment status", description = "Update payment status for multiple orders at once")
    public ResponseEntity<Integer> bulkUpdatePayment(@RequestBody BulkPaymentRequest request) {
        int updatedCount = orderService.bulkUpdatePaymentStatus(request);
        return ResponseEntity.ok(updatedCount);
    }

    //
    @PatchMapping(Constant.CANCEL_ENDPOINT + Constant.ID_PATH_PARAMETER)
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") UUID id) {
        orderService.cancelOrderById(id);
        return ResponseEntity.ok().build();
    }
}
