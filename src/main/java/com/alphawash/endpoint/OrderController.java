package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.OrderTableDto;
import com.alphawash.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constant.API_ORDERS)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping(Constant.ROOT)
    public ResponseEntity<List<OrderTableDto>> getOrders() {
        List<OrderTableDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
