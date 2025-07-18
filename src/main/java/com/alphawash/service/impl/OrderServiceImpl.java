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
import com.alphawash.request.UpdateBasicOrderRequest;
import com.alphawash.service.OrderService;
import com.alphawash.util.DateTimeUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
    public OrderTableDto getFullOrderById(UUID orderId) {
        List<Object[]> result = orderRepository.getFullOrderById(orderId);

        if (result == null || result.isEmpty()) {
            return null; // hoặc throw NotFoundException
        }
        OrderConverter converter = new OrderConverter(employeeRepository);
        return converter.mapFromSingleRow(result.get(0));
    }

    @Override
    @Transactional
    public void createOrder(BasicOrderRequest request) {
        Customer customer = null;
        if (ObjectUtils.isNotNull(request.customer())) {
            if (isBlankUUID(request.customer().id())) {
                customerRepository.findByPhone(request.customer().phone()).ifPresent(existingCustomer -> {
                    throw new BusinessException(
                            HttpStatus.BAD_REQUEST,
                            "Customer with phone " + request.customer().phone() + " already exists.");
                });
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
                                "Customer not found with ID: "
                                        + request.customer().id()));
            }
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

            if (ObjectUtils.isNotNull(request.vehicle().licensePlate())) {
                vehicleRepository
                        .findByLicensePlate(request.vehicle().licensePlate())
                        .ifPresent(existingVehicle -> {
                            throw new BusinessException(
                                    HttpStatus.BAD_REQUEST,
                                    "Vehicle with license plate "
                                            + request.vehicle().licensePlate()
                                            + " already exists.");
                        });
            }
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

    @Override
    @Transactional
    public int updateOrderById(UpdateBasicOrderRequest request) {
        UUID id = request.id();
        BasicOrderRequest basicOrderRequest = request.request();
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found with ID: " + id));
        Customer customer = null;
        if (!Objects.isNull(basicOrderRequest.customer())) {
            if (isBlankUUID(basicOrderRequest.customer().id())
                    && (StringUtils.isNotNullOrEmpty(
                                    basicOrderRequest.customer().name())
                            && StringUtils.isNotNullOrEmpty(
                                    basicOrderRequest.customer().phone()))) {
                // if not found by ID, create a new customer
                var isExistPhoneNumber = customerRepository.findByPhone(
                        basicOrderRequest.customer().phone());

                customer = isExistPhoneNumber.orElseGet(() -> customerRepository.save(Customer.builder()
                        .customerName(basicOrderRequest.customer().name())
                        .phone(basicOrderRequest.customer().phone())
                        .build()));

            } else {
                // if found by ID, retrieve the customer
                if (!isBlankUUID(basicOrderRequest.customer().id())) {
                    customer = customerRepository
                            .findById(basicOrderRequest.customer().id())
                            .orElseThrow(() -> new BusinessException(
                                    HttpStatus.NOT_FOUND,
                                    "Customer not found with ID: "
                                            + basicOrderRequest.customer().id()));
                }
            }
        }

        try {
            updateOrderRequest(basicOrderRequest, order);
            OrderDetail orderDetail = orderDetailRepository
                    .findByOrderId(order.getId())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND, "Order detail not found for order ID: " + order.getId()));
            Vehicle vehicle = null;
            if (basicOrderRequest.vehicle() != null) {
                vehicle = orderRepository
                        .findVehicleByOrderId(order.getId())
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND, "Vehicle not found for order ID: " + order.getId()));
                updateVehicle(basicOrderRequest, vehicle, customer);
            }
            updateOrderDetail(basicOrderRequest, orderDetail, order, vehicle);

        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Failed to update order: " + e.getMessage());
        }
        return 0;
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

    private void updateOrderRequest(BasicOrderRequest request, Order order) {
        ObjectUtils.setIfNotNull(request.information().date(), order::setDate);
        ObjectUtils.setIfNotNull(request.information().checkInTime(), order::setCheckinTime);
        ObjectUtils.setIfNotNull(request.information().paymentStatus(), order::setPaymentStatus);
        ObjectUtils.setIfNotNull(request.information().paymentType(), order::setPaymentType);
        ObjectUtils.setIfNotNull(request.information().tip(), order::setTip);
        ObjectUtils.setIfNotNull(request.information().discount(), order::setDiscount);
        ObjectUtils.setIfNotNull(request.information().vat(), order::setVat);
        ObjectUtils.setIfNotNull(request.information().totalPrice(), order::setTotalPrice);
        ObjectUtils.setIfNotNull(request.information().note(), order::setNote);
        order.setUpdatedAt(DateTimeUtils.getCurrentDate());
        order.setExclusiveKey(order.getExclusiveKey() + 1);
        orderRepository.save(order);
    }

    private void updateOrderDetail(BasicOrderRequest request, OrderDetail orderDetail, Order order, Vehicle vehicle) {
        if (StringUtils.isNotNullOrEmpty(request.service().serviceCatalogCode())) {
            ServiceCatalog serviceCatalog = serviceCatalogRepository
                    .findByCode(request.service().serviceCatalogCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "Service catalog not found with code: "
                                    + request.service().serviceCatalogCode()));
            orderDetail.setServiceCatalog(serviceCatalog);
        }

        ObjectUtils.setIfNotNull(request.information().employeeId(), orderDetail::setEmployeeId);
        ObjectUtils.setIfNotNull(request.information().note(), orderDetail::setNote);
        orderDetail.setOrder(order);
        orderDetail.setVehicle(vehicle);
        orderDetail.setUpdatedAt(DateTimeUtils.getCurrentDate());
        orderDetailRepository.save(orderDetail);
    }

    private void updateVehicle(BasicOrderRequest request, Vehicle vehicle, Customer customer) {
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
        var vehicleResult =
                vehicleRepository.findByLicensePlate(request.vehicle().licensePlate());

        if (vehicleResult.isPresent()) {
            vehicle = vehicleResult.get();
        }

        vehicle.setBrand(brand);
        vehicle.setModel(model);
        ObjectUtils.setIfNotNull(request.vehicle().licensePlate(), vehicle::setLicensePlate);
        ObjectUtils.setIfNotNull(request.vehicle().note(), vehicle::setNote);
        ObjectUtils.setIfNotNull(customer, vehicle::setCustomer);
        vehicle.setUpdatedAt(DateTimeUtils.getCurrentDate());
        vehicleRepository.save(vehicle);
    }

    private boolean isBlankUUID(UUID uuid) {
        return uuid == null || uuid.toString().trim().isEmpty();
    }
}
