package com.alphawash.service.impl;

import com.alphawash.converter.OrderConverter;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.entity.*;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.*;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.service.OrderService;
import com.alphawash.util.DateTimeUtils;
import com.alphawash.util.ObjectUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OrderServiceDtlRepository orderServiceDtlRepository;
    private final OrderConverter orderConverter;

    @Override
    public List<OrderFullDto> getAllOrders() {
        List<Object[]> rawData = orderRepository.getAllOrderRaw();
        return orderConverter.mapToOrderFullDto(rawData, employeeRepository);
    }

    public OrderFullDto getOrderByCode(String code) {
        List<Object[]> rows = orderRepository.findFullByCode(code);
        List<OrderFullDto> result = orderConverter.mapToOrderFullDto(rows, employeeRepository);
        return result.isEmpty() ? null : result.get(0);
    }

    public OrderFullDto getOrderById(UUID id) {
        List<Object[]> rows = orderRepository.findFullById(id);
        List<OrderFullDto> result = orderConverter.mapToOrderFullDto(rows, employeeRepository);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderCreateRequest request) {
        // ===== 1. Kiểm tra khách hàng nếu có =====
        UUID customerId = request.customerId();
        Customer customer = null;
        if (customerId != null) {
            customer = customerRepository
                    .findById(customerId)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }

        // ===== 2. Kiểm tra hoặc tạo mới xe =====
        if (request.licensePlate() == null || request.licensePlate().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        Vehicle vehicle =
                vehicleRepository.findByLicensePlate(request.licensePlate()).orElse(null);

        if (vehicle == null) {
            // Nếu xe mới thì bắt buộc phải có brandCode + modelCode
            if (request.brandCode() == null || request.modelCode() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Thiếu thông tin hãng xe hoặc dòng xe");
            }

            Brand brand = brandRepository
                    .findByCode(request.brandCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại: " + request.brandCode()));

            Model model = modelRepository
                    .findByCode(request.modelCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại: " + request.modelCode()));

            vehicle = Vehicle.builder()
                    .licensePlate(request.licensePlate())
                    .brand(brand)
                    .model(model)
                    .customer(customer)
                    .imageUrl(request.imageUrl())
                    .note(request.vehicleNote())
                    .build();

            vehicleRepository.save(vehicle);
        }

        // ===== 3. Tạo đơn hàng =====
        Order order = new Order();
        order.setCode(generateOrderCode());
        order.setCustomer(customer);
        order.setDate(request.date());
        order.setCheckinTime(request.checkInTime());
        order.setCheckoutTime(request.checkOutTime());
        order.setPaymentStatus(request.paymentStatus());
        order.setPaymentType(request.paymentType());
        order.setVat(request.vat());
        order.setTip(request.tip());
        order.setDiscount(request.discount());
        order.setNote(request.note());
        orderRepository.save(order);

        // ===== 4. Xử lý từng chi tiết đơn hàng =====
        BigDecimal totalServicePrice = BigDecimal.ZERO;

        for (OrderCreateRequest.OrderDetailRequest detailReq : request.orderDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setCode(generateOrderDetailCode());
            detail.setOrder(order);
            detail.setVehicle(vehicle);
            detail.setStatus(detailReq.status());
            detail.setNote(detailReq.note());

            String employeeStr =
                    detailReq.employeeIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            detail.setEmployeeId(employeeStr);

            orderDetailRepository.save(detail);

            // ===== 5. Gán các dịch vụ cho từng chi tiết =====
            for (String scCode : detailReq.serviceCatalogCodes()) {
                ServiceCatalog sc = serviceCatalogRepository
                        .findByCode(scCode)
                        .orElseThrow(() ->
                                new BusinessException(HttpStatus.BAD_REQUEST, "Gói dịch vụ không tồn tại: " + scCode));

                OrderServiceDtl osd = new OrderServiceDtl();
                osd.setCode(generateOrderServiceDtlCode());
                osd.setOrderDetail(detail);
                osd.setServiceCatalogCode(scCode);
                orderServiceDtlRepository.save(osd);

                totalServicePrice = totalServicePrice.add(sc.getPrice());
            }
        }

        // ===== 6. Tính lại tổng tiền và xác minh =====
        order.setTotalPrice(request.totalPrice());
        orderRepository.save(order);
    }

    public String generateOrderCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        long count = orderRepository.countByDate(LocalDate.now()) + 1;
        return "O" + datePart + "-" + String.format("%03d", count);
    }

    public String generateOrderDetailCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        long count = orderDetailRepository.countByDay(LocalDate.now()) + 1;
        return "OD-" + datePart + "-" + String.format("%03d", count);
    }

    public String generateOrderServiceDtlCode() {
        return orderServiceDtlRepository.generateOrderServiceDtlSequenceCode();
    }

    @Override
    @Transactional
    public void updateOrder(OrderUpdateRequest request) {
        // 1. Lấy đơn hàng
        Order order = orderRepository
                .findById(request.orderId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Đơn hàng không tồn tại"));

        // 2. Lấy khách hàng nếu có
        Customer customer = null;
        UUID customerId = request.customerId();
        if (customerId != null) {
            customer = customerRepository
                    .findById(customerId)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }
        order.setCustomer(customer);

        // 3. Lấy hoặc tạo mới xe
        if (request.licensePlate() == null || request.licensePlate().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        Vehicle vehicle =
                vehicleRepository.findByLicensePlate(request.licensePlate()).orElse(null);
        if (vehicle == null) {
            if (request.brandCode() == null || request.modelCode() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Thiếu thông tin hãng hoặc dòng xe khi tạo mới xe");
            }

            Brand brand = brandRepository
                    .findByCode(request.brandCode())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại"));
            Model model = modelRepository
                    .findByCode(request.modelCode())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại"));

            vehicle = Vehicle.builder()
                    .licensePlate(request.licensePlate())
                    .brand(brand)
                    .model(model)
                    .customer(customer)
                    .build();
        }

        // Nếu là xe cũ và người dùng muốn cập nhật thêm thông tin xe
        ObjectUtils.setIfNotNull(request.imageUrl(), vehicle::setImageUrl);
        ObjectUtils.setIfNotNull(request.vehicleNote(), vehicle::setNote);
        vehicleRepository.save(vehicle);

        // 4. Cập nhật order
        ObjectUtils.setIfNotNull(request.paymentStatus(), order::setPaymentStatus);
        ObjectUtils.setIfNotNull(request.paymentType(), order::setPaymentType);
        ObjectUtils.setIfNotNull(request.checkInTime(), order::setCheckinTime);
        ObjectUtils.setIfNotNull(request.checkOutTime(), order::setCheckoutTime);
        ObjectUtils.setIfNotNull(request.vat(), order::setVat);
        ObjectUtils.setIfNotNull(request.tip(), order::setTip);
        ObjectUtils.setIfNotNull(request.discount(), order::setDiscount);
        ObjectUtils.setIfNotNull(request.note(), order::setNote);

        orderRepository.save(order);

        BigDecimal totalServicePrice = BigDecimal.ZERO;

        // 5. Duyệt từng order detail
        for (OrderUpdateRequest.OrderDetailUpdateRequest detailReq : request.orderDetails()) {
            OrderDetail detail = orderDetailRepository
                    .findByCode(detailReq.orderDetailCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.BAD_REQUEST, "Chi tiết đơn hàng không tồn tại: " + detailReq.orderDetailCode()));

            detail.setVehicle(vehicle); // Cập nhật lại xe

            ObjectUtils.setIfNotNull(detailReq.status(), detail::setStatus);
            ObjectUtils.setIfNotNull(detailReq.note(), detail::setNote);

            if (detailReq.employeeIds() != null && !detailReq.employeeIds().isEmpty()) {
                String employeeStr =
                        detailReq.employeeIds().stream().map(String::valueOf).collect(Collectors.joining(","));
                detail.setEmployeeId(employeeStr);
            }

            orderDetailRepository.save(detail);

            // 6. Cập nhật dịch vụ
            if (detailReq.serviceCatalogCodes() != null
                    && !detailReq.serviceCatalogCodes().isEmpty()) {
                List<OrderServiceDtl> existingServices =
                        orderServiceDtlRepository.findByOrderDetailCode(detail.getCode());

                Set<String> existingScCodes = existingServices.stream()
                        .map(OrderServiceDtl::getServiceCatalogCode)
                        .collect(Collectors.toSet());

                Set<String> requestScCodes = new HashSet<>(detailReq.serviceCatalogCodes());

                // 1. Xoá cái không còn
                Set<String> codesToRemove = new HashSet<>(existingScCodes);
                codesToRemove.removeAll(requestScCodes);

                if (!codesToRemove.isEmpty()) {
                    orderServiceDtlRepository.deleteByOrderDetailCodeAndServiceCatalogCodes(
                            detail.getCode(), codesToRemove);
                }

                // 2. Thêm cái mới
                Set<String> codesToAdd = new HashSet<>(requestScCodes);
                codesToAdd.removeAll(existingScCodes);

                for (String scCode : codesToAdd) {
                    ServiceCatalog sc = serviceCatalogRepository
                            .findByCode(scCode)
                            .orElseThrow(() -> new BusinessException(
                                    HttpStatus.BAD_REQUEST, "Gói dịch vụ không tồn tại: " + scCode));

                    OrderServiceDtl osd = OrderServiceDtl.builder()
                            .code(generateOrderServiceDtlCode())
                            .orderDetail(detail)
                            .serviceCatalogCode(scCode)
                            .build();

                    orderServiceDtlRepository.save(osd);
                    totalServicePrice = totalServicePrice.add(sc.getPrice());
                }
            }
        }

        order.setTotalPrice(request.totalPrice());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrderById(UUID orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        order.setDeleteFlag(true);
        order.setUpdatedAt(DateTimeUtils.getCurrentDate());
        orderRepository.save(order);
    }
}
