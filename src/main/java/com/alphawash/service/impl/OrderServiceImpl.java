package com.alphawash.service.impl;

import com.alphawash.converter.OrderConverter;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.Model;
import com.alphawash.entity.Order;
import com.alphawash.entity.OrderDetail;
import com.alphawash.entity.OrderProductDtl;
import com.alphawash.entity.OrderServiceDtl;
import com.alphawash.entity.Product;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.OrderProductDtlRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.OrderServiceDtlRepository;
import com.alphawash.repository.ProductRepository;
import com.alphawash.repository.ServiceCatalogRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.BulkPaymentRequest;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.request.ProductOrderItemRequest;
import com.alphawash.service.InventoryService;
import com.alphawash.service.OrderService;
import com.alphawash.response.PageResponse;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.DateTimeUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
    private final OrderProductDtlRepository orderProductDtlRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final OrderConverter orderConverter;

    @Override
    public List<OrderFullDto> getAllOrders() {
        List<Object[]> rawData = orderRepository.getAllOrderRaw();
        return orderConverter.mapToOrderFullDto(rawData, employeeRepository);
    }

    @Override
    public PageResponse<OrderFullDto> getOrdersPaged(int page, int size) {
        int offset = page * size;
        List<Object[]> rawData = orderRepository.getAllOrderRawPaged(size, offset);
        List<OrderFullDto> orders = orderConverter.mapToOrderFullDto(rawData, employeeRepository);
        long totalElements = orderRepository.countAllOrdersForPaging();
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return PageResponse.<OrderFullDto>builder()
                .content(orders)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
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
    public UUID createOrder(OrderCreateRequest request) {
        // ===== 1. Kiểm tra khách hàng nếu có =====
        UUID customerId = request.customerId();
        Customer customer = null;
        if (customerId != null) {
            customer = customerRepository
                    .findById(customerId)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }

        // ===== 2. Tạo đơn hàng =====
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

        // ===== 3. Xử lý từng chi tiết đơn hàng (mỗi detail = 1 xe) =====
        BigDecimal totalServicePrice = BigDecimal.ZERO;

        for (OrderCreateRequest.OrderDetailRequest detailReq : request.orderDetails()) {
            // ===== 3.1 Kiểm tra hoặc tạo mới xe cho từng detail =====
            if (detailReq.licensePlate() == null || detailReq.licensePlate().isBlank()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
            }
            
            Vehicle vehicle = vehicleRepository
                    .findByLicensePlate(detailReq.licensePlate())
                    .orElse(null);

            if (vehicle == null) {
                // Nếu xe mới thì bắt buộc phải có brandCode + modelCode
                if (detailReq.brandCode() == null || detailReq.modelCode() == null) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, 
                        "Thiếu thông tin hãng xe hoặc dòng xe cho biển số: " + detailReq.licensePlate());
                }

                Brand brand = brandRepository
                        .findByCode(detailReq.brandCode())
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại: " + detailReq.brandCode()));

                Model model = modelRepository
                        .findByCode(detailReq.modelCode())
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại: " + detailReq.modelCode()));

                vehicle = Vehicle.builder()
                        .licensePlate(detailReq.licensePlate())
                        .brand(brand)
                        .model(model)
                        .customer(customer)
                        .imageUrl(detailReq.imageUrl())
                        .note(detailReq.vehicleNote())
                        .build();

                vehicleRepository.save(vehicle);
            }

            // ===== 3.2 Tạo order detail =====
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
            if (CollectionUtils.isNotEmpty(detailReq.services())) {
                for (OrderCreateRequest.ServiceCreateRequest serviceReq : detailReq.services()) {
                    ServiceCatalog sc = serviceCatalogRepository
                            .findByCode(serviceReq.serviceCatalogCode())
                            .orElseThrow(() -> new BusinessException(
                                    HttpStatus.BAD_REQUEST,
                                    "Gói dịch vụ không tồn tại: " + serviceReq.serviceCatalogCode()));

                    OrderServiceDtl osd = new OrderServiceDtl();
                    osd.setCode(generateOrderServiceDtlCode());
                    osd.setOrderDetail(detail);
                    osd.setServiceCatalogCode(serviceReq.serviceCatalogCode());
                    if (Boolean.TRUE.equals(serviceReq.adjustedPriceFlag())) {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(true);
                        osd.setAdjustedPriceReason(serviceReq.adjustedPriceReason());
                    } else {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(false);
                        osd.setAdjustedPriceReason(null);
                    }
                    osd.setQuantity(serviceReq.quantity() != null && serviceReq.quantity() >= 1
                            ? serviceReq.quantity() : 1);
                    orderServiceDtlRepository.save(osd);
                }
            }

            // ===== 6. Gán các sản phẩm cho từng chi tiết =====
            if (CollectionUtils.isNotEmpty(detailReq.products())) {
                for (ProductOrderItemRequest productReq : detailReq.products()) {
                    Product product = productRepository
                            .findByCode(productReq.getProductCode())
                            .orElseThrow(() -> new BusinessException(
                                    HttpStatus.BAD_REQUEST,
                                    "Sản phẩm không tồn tại: " + productReq.getProductCode()));

                    OrderProductDtl opd = new OrderProductDtl();
                    opd.setCode(generateOrderProductDtlCode());
                    opd.setOrderDetail(detail);
                    opd.setProduct(product);
                    opd.setQuantity(productReq.getQuantity() != null && productReq.getQuantity() >= 1
                            ? productReq.getQuantity() : 1);
                    opd.setUnitPrice(productReq.getUnitPrice() != null
                            ? productReq.getUnitPrice() : product.getSellingPrice());
                    opd.setAdjustedPrice(productReq.getAdjustedPrice());
                    opd.setAdjustedPriceFlag(Boolean.TRUE.equals(productReq.getAdjustedPriceFlag()));
                    opd.setAdjustedPriceReason(
                            Boolean.TRUE.equals(productReq.getAdjustedPriceFlag())
                                    ? productReq.getAdjustedPriceReason() : null);
                    opd.setDiscount(productReq.getDiscount());
                    opd.setNote(productReq.getNote());
                    orderProductDtlRepository.save(opd);

                    if (Boolean.TRUE.equals(product.getTrackInventory())) {
                        inventoryService.deductStock(
                                product.getCode(), opd.getQuantity(), order.getCode());
                    }
                }
            }
        }

        // ===== 4. Lưu tổng tiền đã được tính trên Fe =====
        order.setTotalPrice(request.totalPrice());
        orderRepository.save(order);
        return order.getId();
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

    public String generateOrderProductDtlCode() {
        String datePrefix = "OPD" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        long count = orderProductDtlRepository.countByCodeStartingWith(datePrefix);
        return String.format("%s-%04d", datePrefix, count + 1);
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
        if (customerId != null && !StringUtils.isUUIDNullOrBlank(customerId)) {
            customer = customerRepository
                    .findById(customerId)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }
        order.setCustomer(customer);

        // 3. Cập nhật order
        ObjectUtils.setIfNotNull(request.paymentStatus(), order::setPaymentStatus);
        ObjectUtils.setIfNotNull(request.paymentType(), order::setPaymentType);
        ObjectUtils.setIfNotNull(request.checkInTime(), order::setCheckinTime);
        ObjectUtils.setIfNotNull(request.checkOutTime(), order::setCheckoutTime);
        ObjectUtils.setIfNotNull(request.date(), order::setDate);
        ObjectUtils.setIfNotNull(request.vat(), order::setVat);
        ObjectUtils.setIfNotNull(request.tip(), order::setTip);
        ObjectUtils.setIfNotNull(request.discount(), order::setDiscount);
        ObjectUtils.setIfNotNull(request.note(), order::setNote);

        orderRepository.save(order);

        BigDecimal totalServicePrice = BigDecimal.ZERO;

        // 4. Duyệt từng order detail — hỗ trợ cả detail cũ (update) và detail mới (tạo thêm xe)
        for (OrderUpdateRequest.OrderDetailUpdateRequest detailReq : request.orderDetails()) {

            // 4a. Xác định vehicle cho detail này
            String plateSrc = detailReq.licensePlate() != null && !detailReq.licensePlate().isBlank()
                    ? detailReq.licensePlate()
                    : request.licensePlate(); // fallback về top-level nếu FE cũ không gửi per-detail

            if (StringUtils.isNullOrBlank(plateSrc)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
            }

            Vehicle detailVehicle = vehicleRepository.findByLicensePlate(plateSrc).orElse(null);

            if (detailVehicle == null) {
                // Xe chưa tồn tại → tạo mới
                String brandSrc = detailReq.brandCode() != null ? detailReq.brandCode() : request.brandCode();
                String modelSrc = detailReq.modelCode() != null ? detailReq.modelCode() : request.modelCode();

                if (StringUtils.isNullOrBlank(brandSrc) || StringUtils.isNullOrBlank(modelSrc)) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST,
                            "Thiếu thông tin hãng hoặc dòng xe cho biển số: " + plateSrc);
                }

                Brand brand = brandRepository
                        .findByCode(brandSrc)
                        .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại: " + brandSrc));
                Model model = modelRepository
                        .findByCode(modelSrc)
                        .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại: " + modelSrc));

                detailVehicle = Vehicle.builder()
                        .licensePlate(plateSrc)
                        .brand(brand)
                        .model(model)
                        .customer(customer)
                        .imageUrl(detailReq.imageUrl() != null ? detailReq.imageUrl() : request.imageUrl())
                        .note(detailReq.vehicleNote() != null ? detailReq.vehicleNote() : request.vehicleNote())
                        .build();
                vehicleRepository.save(detailVehicle);
            } else {
                // Xe đã tồn tại → cập nhật thêm thông tin nếu có
                String imgSrc = detailReq.imageUrl() != null ? detailReq.imageUrl() : request.imageUrl();
                String noteSrc = detailReq.vehicleNote() != null ? detailReq.vehicleNote() : request.vehicleNote();
                ObjectUtils.setIfNotNull(imgSrc, detailVehicle::setImageUrl);
                ObjectUtils.setIfNotNull(noteSrc, detailVehicle::setNote);
                vehicleRepository.save(detailVehicle);
            }

            // 4b. Tìm hoặc tạo mới OrderDetail
            OrderDetail detail;
            boolean isNewDetail = detailReq.orderDetailCode() == null || detailReq.orderDetailCode().isBlank();

            if (isNewDetail) {
                // Detail mới — tạo mới (xe thêm vào trong mode edit)
                detail = new OrderDetail();
                detail.setCode(generateOrderDetailCode());
                detail.setOrder(order);
                detail.setStatus(detailReq.status() != null ? detailReq.status() : "PENDING");
            } else {
                detail = orderDetailRepository
                        .findByCode(detailReq.orderDetailCode())
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.BAD_REQUEST, "Chi tiết đơn hàng không tồn tại: " + detailReq.orderDetailCode()));
                ObjectUtils.setIfNotNull(detailReq.status(), detail::setStatus);
            }

            detail.setVehicle(detailVehicle);

            ObjectUtils.setIfNotNull(detailReq.note(), detail::setNote);

            if (detailReq.employeeIds() != null && !detailReq.employeeIds().isEmpty()) {
                String employeeStr =
                        detailReq.employeeIds().stream().map(String::valueOf).collect(Collectors.joining(","));
                detail.setEmployeeId(employeeStr);
            }

            orderDetailRepository.save(detail);

            // 6. Cập nhật dịch vụ
            if (CollectionUtils.isNotEmpty(detailReq.services())) {
                List<OrderServiceDtl> existingServices =
                        orderServiceDtlRepository.findByOrderDetailCode(detail.getCode());

                Map<String, OrderServiceDtl> existingMap = existingServices.stream()
                        .collect(Collectors.toMap(OrderServiceDtl::getServiceCatalogCode, s -> s));

                Set<String> requestScCodes = detailReq.services().stream()
                        .map(OrderUpdateRequest.ServiceUpdateRequest::serviceCatalogCode)
                        .collect(Collectors.toSet());

                // 1. Xoá service không còn trong request
                Set<String> codesToRemove = new HashSet<>(existingMap.keySet());
                codesToRemove.removeAll(requestScCodes);
                if (!codesToRemove.isEmpty()) {
                    orderServiceDtlRepository.deleteByOrderDetailCodeAndServiceCatalogCodes(
                            detail.getCode(), codesToRemove);
                }

                // 2. Thêm hoặc cập nhật service trong request
                for (OrderUpdateRequest.ServiceUpdateRequest serviceReq : detailReq.services()) {
                    OrderServiceDtl osd = existingMap.get(serviceReq.serviceCatalogCode());

                    if (osd == null) {
                        // Thêm mới
                        osd = OrderServiceDtl.builder()
                                .code(generateOrderServiceDtlCode())
                                .orderDetail(detail)
                                .serviceCatalogCode(serviceReq.serviceCatalogCode())
                                .build();
                    }

                    // Cập nhật thông tin giá
                    osd.setAdjustedPrice(serviceReq.adjustedPrice());
                    osd.setAdjustedPriceFlag(Boolean.TRUE.equals(serviceReq.adjustedPriceFlag()));
                    osd.setAdjustedPriceReason(
                            Boolean.TRUE.equals(serviceReq.adjustedPriceFlag())
                                    ? serviceReq.adjustedPriceReason()
                                    : null);
                    osd.setQuantity(serviceReq.quantity() != null && serviceReq.quantity() >= 1
                            ? serviceReq.quantity() : 1);

                    orderServiceDtlRepository.save(osd);

                    // Luôn cộng adjustedPrice (FE đã gửi giá gốc hoặc giá điều chỉnh)
                    if (serviceReq.adjustedPrice() != null) {
                        totalServicePrice = totalServicePrice.add(serviceReq.adjustedPrice());
                    }
                }
            }

            // 7. Cập nhật sản phẩm
            if (detailReq.products() != null) {
                List<OrderProductDtl> existingProducts =
                        orderProductDtlRepository.findByOrderDetail_Code(detail.getCode());

                Map<String, OrderProductDtl> existingProductMap = existingProducts.stream()
                        .collect(Collectors.toMap(
                                opd -> opd.getProduct().getCode(), opd -> opd));

                Set<String> requestProductCodes = detailReq.products().stream()
                        .map(ProductOrderItemRequest::getProductCode)
                        .collect(Collectors.toSet());

                // Remove products no longer in request — restore inventory
                for (OrderProductDtl existing : existingProducts) {
                    if (!requestProductCodes.contains(existing.getProduct().getCode())) {
                        if (Boolean.TRUE.equals(existing.getProduct().getTrackInventory())) {
                            inventoryService.addStock(
                                    existing.getProduct().getCode(),
                                    existing.getQuantity(),
                                    order.getCode());
                        }
                        existing.setDeleteFlag(true);
                        orderProductDtlRepository.save(existing);
                    }
                }

                // Add or update products
                for (ProductOrderItemRequest productReq : detailReq.products()) {
                    Product product = productRepository
                            .findByCode(productReq.getProductCode())
                            .orElseThrow(() -> new BusinessException(
                                    HttpStatus.BAD_REQUEST,
                                    "Sản phẩm không tồn tại: " + productReq.getProductCode()));

                    OrderProductDtl opd = existingProductMap.get(productReq.getProductCode());
                    int oldQty = 0;

                    if (opd == null) {
                        opd = new OrderProductDtl();
                        opd.setCode(generateOrderProductDtlCode());
                        opd.setOrderDetail(detail);
                        opd.setProduct(product);
                    } else {
                        oldQty = opd.getQuantity() != null ? opd.getQuantity() : 0;
                    }

                    int newQty = productReq.getQuantity() != null && productReq.getQuantity() >= 1
                            ? productReq.getQuantity() : 1;

                    opd.setQuantity(newQty);
                    opd.setUnitPrice(productReq.getUnitPrice() != null
                            ? productReq.getUnitPrice() : product.getSellingPrice());
                    opd.setAdjustedPrice(productReq.getAdjustedPrice());
                    opd.setAdjustedPriceFlag(Boolean.TRUE.equals(productReq.getAdjustedPriceFlag()));
                    opd.setAdjustedPriceReason(
                            Boolean.TRUE.equals(productReq.getAdjustedPriceFlag())
                                    ? productReq.getAdjustedPriceReason() : null);
                    opd.setDiscount(productReq.getDiscount());
                    opd.setNote(productReq.getNote());
                    orderProductDtlRepository.save(opd);

                    // Adjust inventory for quantity changes
                    if (Boolean.TRUE.equals(product.getTrackInventory())) {
                        int diff = newQty - oldQty;
                        if (diff > 0) {
                            inventoryService.deductStock(product.getCode(), diff, order.getCode());
                        } else if (diff < 0) {
                            inventoryService.addStock(product.getCode(), -diff, order.getCode());
                        }
                    }
                }
            }
        }

        order.setTotalPrice(request.totalPrice());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public int bulkUpdatePaymentStatus(BulkPaymentRequest request) {
        if (request.orderIds() == null || request.orderIds().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Danh sách đơn hàng không được để trống");
        }
        if (StringUtils.isNullOrBlank(request.paymentStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Trạng thái thanh toán không được để trống");
        }

        List<Order> orders = orderRepository.findByIdIn(request.orderIds());
        if (orders.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy đơn hàng nào");
        }

        for (Order order : orders) {
            order.setPaymentStatus(request.paymentStatus());
        }
        orderRepository.saveAll(orders);

        return orders.size();
    }

    @Override
    @Transactional
    public void cancelOrderById(UUID orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();

        // Restore inventory for all product items
        List<OrderDetail> details = orderDetailRepository.findByOrder_Code(order.getCode());
        for (OrderDetail detail : details) {
            List<OrderProductDtl> productItems =
                    orderProductDtlRepository.findByOrderDetail_CodeAndDeleteFlagFalse(detail.getCode());
            for (OrderProductDtl opd : productItems) {
                if (opd.getProduct() != null && Boolean.TRUE.equals(opd.getProduct().getTrackInventory())) {
                    inventoryService.addStock(
                            opd.getProduct().getCode(), opd.getQuantity(), order.getCode());
                }
            }
        }

        order.setDeleteFlag(true);
        order.setUpdatedAt(DateTimeUtils.getCurrentDate());
        orderRepository.save(order);
    }
}
