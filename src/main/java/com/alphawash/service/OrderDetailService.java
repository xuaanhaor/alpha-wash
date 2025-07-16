package com.alphawash.service;

import com.alphawash.response.OrderServiceDetailResponse;
import java.util.List;

public interface OrderDetailService {
    List<OrderServiceDetailResponse> getAllOrderDetails();
}
