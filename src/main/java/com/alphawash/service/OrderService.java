package com.alphawash.service;

import com.alphawash.dto.OrderTableDto;
import java.util.List;

public interface OrderService {
    List<OrderTableDto> getAllOrders();
}
