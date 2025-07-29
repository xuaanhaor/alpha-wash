package com.alphawash.service;

import com.alphawash.dto.OrderFullDto;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderFullDto> getAllOrders();

    OrderFullDto getOrderById(UUID id);

    OrderFullDto getOrderByCode(String code);

    UUID createOrder(OrderCreateRequest request);

    void updateOrder(OrderUpdateRequest request);

    void cancelOrderById(UUID orderId);
}
