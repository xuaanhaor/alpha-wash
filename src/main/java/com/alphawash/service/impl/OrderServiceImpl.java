package com.alphawash.service.impl;

import com.alphawash.converter.OrderConverter;
import com.alphawash.dto.OrderTableDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.Model;
import com.alphawash.entity.Order;
import com.alphawash.entity.OrderDetail;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.ServiceCatalogRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.BasicOrderRequest;
import com.alphawash.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderTableDto> getAllOrders() {
        List<Object[]> rawData = orderRepository.getAllOrdersRaw();

        OrderConverter converter = new OrderConverter(employeeRepository);
        return converter.mapFromRawObjectList(rawData);
    }

    @Override
    @Transactional
    public void createOrder(BasicOrderRequest request) {
        Customer customer;
        if (isBlankUUID(request.customer().id())) {
            // if not found by ID, create a new customer
            customer = customerRepository.save(Customer.builder()
                    .customerName(request.customer().name())
                    .phone(request.customer().phone())
                    .build());
        } else {
            // if found by ID, retrieve the customer
            customer = customerRepository
                    .findById(request.customer().id())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "Customer not found with ID: " + request.customer().id()));
        }
        try {
            ServiceCatalog serviceCatalog = serviceCatalogRepository
                    .findByCode(request.service().serviceCatalogCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "Service catalog not found with code: "
                                    + request.service().serviceCatalogCode()));
            Brand brand = brandRepository
                    .findByCode(request.vehicle().brandCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "Brand not found with code: " + request.vehicle().brandCode()));
            Model model = modelRepository
                    .findByCode(request.vehicle().modelCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "Model not found with code: " + request.vehicle().modelCode()));
            Order order = insertOrder(request);

            Vehicle vehicle = Vehicle.builder()
                    .licensePlate(request.vehicle().licensePlate())
                    .brand(brand)
                    .model(model)
                    .customer(customer)
                    .build();
            vehicleRepository.save(vehicle);

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .employeeId(request.information().employeeId())
                    .vehicle(vehicle)
                    .serviceCatalog(serviceCatalog)
                    .note(order.getNote())
                    .build();
            orderDetailRepository.save(orderDetail);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Failed to create order: " + e.getMessage());
        }
    }

    private Order insertOrder(BasicOrderRequest request) {
        Order order = Order.builder()
                .date(request.information().date())
                .checkinTime(request.information().checkInTime())
                .paymentType(request.information().paymentType())
                .paymentStatus(request.information().paymentStatus())
                .tip(request.information().tip() != null ? request.information().tip() : BigDecimal.ZERO)
                .discount(
                        request.information().discount() != null
                                ? request.information().discount()
                                : BigDecimal.ZERO)
                .vat(request.information().vat())
                .totalPrice(request.information().totalPrice())
                .note(request.information().note())
                .build();
        return orderRepository.save(order);
    }

    private boolean isBlankUUID(UUID uuid) {
        return uuid == null || uuid.toString().trim().isEmpty();
    }
}
