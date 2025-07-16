package com.alphawash.service.impl;

import com.alphawash.converter.OrderConverter;
import com.alphawash.dto.OrderTableDto;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.service.ModelService;
import com.alphawash.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<OrderTableDto> getAllOrders() {
        List<Object[]> rawData = orderRepository.getAllOrdersRaw();

        OrderConverter converter = new OrderConverter(employeeRepository);
        return converter.mapFromRawObjectList(rawData);
    }
}
