package com.alphawash.service;

import com.alphawash.dto.OrderFullDto;
import com.alphawash.request.BulkPaymentRequest;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.response.PageResponse;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderFullDto> getAllOrders();

    PageResponse<OrderFullDto> getOrdersPaged(int page, int size);

    OrderFullDto getOrderById(UUID id);

    OrderFullDto getOrderByCode(String code);

    UUID createOrder(OrderCreateRequest request);

    void updateOrder(OrderUpdateRequest request);

    void cancelOrderById(UUID orderId);

    int bulkUpdatePaymentStatus(BulkPaymentRequest request);
}
