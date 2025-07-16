package com.alphawash.service;

import com.alphawash.dto.OrderTableDto;
import com.alphawash.request.BasicOrderRequest;
import java.util.List;

public interface OrderService {
    List<OrderTableDto> getAllOrders();

    void createOrder(BasicOrderRequest request);
}
