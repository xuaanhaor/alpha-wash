package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
            summary = "Get all orders",
            description = "Returns a list of all orders including customer, vehicle, service, and employee details")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<OrderFullDto>> getOrders() {
        List<OrderFullDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
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
    public ResponseEntity<String> createOrder(@RequestBody OrderCreateRequest request) {
        String code = orderService.createOrder(request);
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Không thể tạo đơn hàng");
        }
        return ResponseEntity.ok(code);
    }

    @PatchMapping(Constant.UPDATE_ENDPOINT)
    public ResponseEntity<Void> updateOrder(@RequestBody OrderUpdateRequest request) {
        orderService.updateOrder(request);
        return ResponseEntity.ok().build();
    }
    //
    @PatchMapping(Constant.CANCEL_ENDPOINT + Constant.ID_PATH_PARAMETER)
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") UUID id) {
        orderService.cancelOrderById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(Constant.CANCEL_ENDPOINT + Constant.ID_PATH_PARAMETER)
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") UUID id) {
        orderService.cancelOrderById(id);
        return ResponseEntity.ok().build();
    }
}
