package com.alphawash.service;

import com.alphawash.dto.OrderFullDto;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderTableDto> getAllOrders();

    OrderTableDto getFullOrderById(UUID orderId);

    void createOrder(BasicOrderRequest request);

    int updateOrderById(UpdateBasicOrderRequest request);

    void cancelOrderById(UUID orderId);
}
