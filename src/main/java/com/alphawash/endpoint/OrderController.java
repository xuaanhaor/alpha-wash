package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.OrderTableDto;
import com.alphawash.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
    public ResponseEntity<List<OrderTableDto>> getOrders() {
        List<OrderTableDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
