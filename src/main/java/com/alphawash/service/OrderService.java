package com.alphawash.service;

import com.alphawash.dto.OrderTableDto;
import com.alphawash.request.BasicOrderRequest;
import com.alphawash.request.UpdateBasicOrderRequest;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderTableDto> getAllOrders();

    OrderTableDto getFullOrderById(UUID orderId);

    void createOrder(BasicOrderRequest request);

    int updateOrderById(UpdateBasicOrderRequest request);

    void cancelOrderById(UUID orderId);
}
