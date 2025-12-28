package com.alphawash.service.impl;

import com.alphawash.constant.OrderType;
import com.alphawash.constant.PromotionStatus;
import com.alphawash.converter.OrderConverter;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.CustomerComboQuota;
import com.alphawash.entity.CustomerComboSummary;
import com.alphawash.entity.CustomerPromotion;
import com.alphawash.entity.Model;
import com.alphawash.entity.Order;
import com.alphawash.entity.OrderDetail;
import com.alphawash.entity.OrderServiceDtl;
import com.alphawash.entity.Promotion;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.entity.ServiceCombo;
import com.alphawash.entity.ServiceComboCatalog;
import com.alphawash.entity.ServiceComboQuality;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.CustomerComboQuotaRepository;
import com.alphawash.repository.CustomerComboSummaryRepository;
import com.alphawash.repository.CustomerPromotionRepository;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.OrderServiceDtlRepository;
import com.alphawash.repository.PromotionRepository;
import com.alphawash.repository.ServiceCatalogRepository;
import com.alphawash.repository.ServiceComboCatalogRepository;
import com.alphawash.repository.ServiceComboQualityRepository;
import com.alphawash.repository.ServiceComboRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.OrderCreateRequest;
import com.alphawash.request.OrderUpdateRequest;
import com.alphawash.service.OrderService;
import com.alphawash.service.VehicleService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.DateTimeUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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
    private final CustomerPromotionRepository customerPromotionRepository;
    private final PromotionRepository promotionRepository;
    private final CustomerComboSummaryRepository customerComboSummaryRepository;
    private final CustomerComboQuotaRepository customerComboQuotaRepository;
    private final ServiceComboRepository serviceComboRepository;
    private final ServiceComboQualityRepository serviceComboQualityRepository;
    private final ServiceComboCatalogRepository serviceComboCatalogRepository;

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
    public UUID createOrder(OrderCreateRequest request) {

        // ===== 0. Null-safe orderDetails =====
        List<OrderCreateRequest.OrderDetailRequest> orderDetails = Optional.ofNullable(request.orderDetails()).orElse(List.of());

        // ===== 1. Kiểm tra khách hàng nếu có =====
        UUID customerId = request.customerId();
        Customer customer = null;
        if (customerId != null) {
            customer = customerRepository.findById(customerId).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }

        // ===== 2. Xử lý xe =====
        Vehicle vehicle = handleVehicleOnOrder(customer, request.licensePlate(), request.brandCode(), request.modelCode(), request.imageUrl(), request.vehicleNote());

        // ===== 3. Tạo Order =====
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

        // createdComboCard: combo card vừa mua trong request (support USE_NEW_COMBO)
        CustomerComboSummary createdComboCard = null;

        // ===== 4. Xử lý từng OrderDetail =====
        for (OrderCreateRequest.OrderDetailRequest detailReq : orderDetails) {

            // ===== 4.1 Tạo OrderDetail =====
            OrderDetail detail = new OrderDetail();
            detail.setCode(generateOrderDetailCode());
            detail.setOrder(order);
            detail.setOrderType(OrderType.valueOf(detailReq.orderType()));
            detail.setVehicle(vehicle);
            detail.setStatus(detailReq.status());
            detail.setNote(detailReq.note());

            // Null-safe employeeIds
            String employeeStr = Optional.ofNullable(detailReq.employeeIds()).orElse(List.of()).stream().map(String::valueOf).collect(Collectors.joining(","));
            detail.setEmployeeId(employeeStr);

            orderDetailRepository.save(detail);

            // ===== 4.2 Parse orderType =====
            // Chỉ cho phép 3 loại: COMBO / SERVICE / USE_COMBO
            String orderType = (StringUtils.isNullOrBlank(detailReq.orderType())) ? "SERVICE" : detailReq.orderType().trim().toUpperCase();
            if (!"COMBO".equals(orderType) && !"SERVICE".equals(orderType) && !"USE_COMBO".equals(orderType)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "orderType không hợp lệ: " + orderType);
            }

            // Null-safe services
            List<OrderCreateRequest.ServiceCreateRequest> services = Optional.ofNullable(detailReq.services()).orElse(List.of());

            // =================================================================
            // CASE 1: COMBO (Mua combo)
            // =================================================================
            if ("COMBO".equals(orderType)) {

                // 1.1 Mua combo bắt buộc có khách hàng
                if (customerId == null) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Mua combo yêu cầu chọn khách hàng");
                }

                // 1.2 Điều kiện chỉ 1 dòng services[] để truyền comboCatalogCode (+ optional price)
                if (services.size() != 1) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Mua combo chỉ cần 1 dòng services[] để truyền comboCatalogCode");
                }

                OrderCreateRequest.ServiceCreateRequest comboReq = services.get(0);

                // 1.3 Lấy comboCatalogCode từ services[0].serviceComboCatalogCode
                String comboCatalogCode = comboReq.serviceComboCatalogCode();
                if (StringUtils.isNullOrBlank(comboCatalogCode)) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Mua combo phải cung cấp mã comboCatalogCode");
                }

                // 1.4 Kiểm tra combo catalog tồn tại
                ServiceComboCatalog combo = serviceComboCatalogRepository.findByCode(comboCatalogCode).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Combo template không tồn tại: " + comboCatalogCode));

                // 1.4.1 Xác định giá bán combo (có thể khác có gốc)
                // - Nếu FE bật adjustedPriceFlag: dùng adjustedPrice làm giá bán
                // - Nếu không: fallback giá mặc định (tùy hệ thống bạn lưu ở đâu)
                BigDecimal soldPrice = null;

                if (Boolean.TRUE.equals(comboReq.adjustedPriceFlag())) {
                    if (comboReq.adjustedPrice() == null || comboReq.adjustedPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "Giá combo điều chỉnh không hợp lệ");
                    }
                    // TODO: set giá thay dổi được truyền xuống từ FE
                    soldPrice = comboReq.adjustedPrice();
                } else {
                    // TODO: set giá mặc định từ comboCatalog
                    soldPrice = combo.getPrice();
                }

                String priceReason = Boolean.TRUE.equals(comboReq.adjustedPriceFlag()) ? comboReq.adjustedPriceReason() : null;

                // 1.5 Tạo combo card + quota
                // -> CẦN sửa createCustomerComboCard để nhận soldPrice + reason và lưu xuống DB
                createdComboCard = createCustomerComboCard(order, customer, combo, soldPrice, priceReason, comboReq.adjustedPriceFlag());

                createCustomerComboQuota(createdComboCard);

                // 1.6 Tạo dòng dịch vụ
                OrderServiceDtl osd = new OrderServiceDtl();
                osd.setCode(generateOrderServiceDtlCode());
                osd.setOrderDetail(detail);
                osd.setServiceComboCatalogCode(comboCatalogCode);
                if (Boolean.TRUE.equals(comboReq.adjustedPriceFlag())) {
                    osd.setAdjustedPrice(comboReq.adjustedPrice());
                    osd.setAdjustedPriceFlag(true);
                    osd.setAdjustedPriceReason(comboReq.adjustedPriceReason());
                } else {
                    osd.setAdjustedPrice(comboReq.adjustedPrice());
                    osd.setAdjustedPriceFlag(false);
                    osd.setAdjustedPriceReason(null);
                }
                orderServiceDtlRepository.save(osd);
                continue;
            }

            // =================================================================
            // CASE 2: SERVICE (Dịch vụ trả tiền lẻ - KHÔNG dùng combo)
            // =================================================================
            if ("SERVICE".equals(orderType)) {

                // 2.0 Không có dịch vụ thì bỏ qua (tùy nghiệp vụ bạn muốn throw hay not)
                if (services.isEmpty()) {
                    continue;
                }

                for (OrderCreateRequest.ServiceCreateRequest serviceReq : services) {

                    // 2.1 Kiểm tra xem service tồn tại
                    ServiceCatalog sc = serviceCatalogRepository.findByCode(serviceReq.serviceCatalogCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Gói dịch vụ không tồn tại: " + serviceReq.serviceCatalogCode()));

                    // 2.2 SERVICE không cho dùng combo (nếu FE lỡ gửi CC/USE_NEW_COMBO → chặn)
                    String comboCardCode = normalizeComboCardCode(serviceReq.serviceComboCatalogCode(), createdComboCard);
                    if (comboCardCode != null) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "orderType=SERVICE không dùng combo. Hãy đổi orderType=USE_COMBO nếu muốn dùng thẻ combo: " + comboCardCode);
                    }

                    // 2.3 Tạo dòng dịch vụ
                    OrderServiceDtl osd = new OrderServiceDtl();
                    osd.setCode(generateOrderServiceDtlCode());
                    osd.setOrderDetail(detail);
                    osd.setServiceCatalogCode(serviceReq.serviceCatalogCode());

                    // 2.4 Adjusted price (giữ logic cũ)
                    if (Boolean.TRUE.equals(serviceReq.adjustedPriceFlag())) {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(true);
                        osd.setAdjustedPriceReason(serviceReq.adjustedPriceReason());
                    } else {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(false);
                        osd.setAdjustedPriceReason(null);
                    }

                    // 2.5 Lưu dòng dịch vụ
                    orderServiceDtlRepository.save(osd);
                }

                // kết thúc xử lý detail SERVICE
                continue;
            }

            // =================================================================
            // CASE 3: USE_COMBO (Dịch vụ dùng combo - BẮT BUỘC có comboCardCode)
            // =================================================================
            if ("USE_COMBO".equals(orderType)) {

                // 3.0 Không có dịch vụ thì bỏ qua (tùy nghiệp vụ bạn muốn throw hay not)
                if (services.isEmpty()) {
                    continue;
                }

                for (OrderCreateRequest.ServiceCreateRequest serviceReq : services) {

                    // 3.1 Kiểm tra xem service tồn tại
                    ServiceCatalog sc = serviceCatalogRepository.findByCode(serviceReq.serviceCatalogCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Gói dịch vụ không tồn tại: " + serviceReq.serviceCatalogCode()));

                    // 3.2 Parse combo card code (CC... / USE_NEW_COMBO)
                    // NOTE: field serviceComboCatalogCode trong USE_COMBO đang mang nghĩa "comboCardCode"
                    String comboCardCode = normalizeComboCardCode(serviceReq.serviceComboCatalogCode(), createdComboCard);

                    // 3.3 USE_COMBO bắt buộc phải có comboCardCode
                    if (comboCardCode == null) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "orderType=USE_COMBO yêu cầu truyền mã thẻ combo (CCxxx) hoặc USE_NEW_COMBO cho dịch vụ: " + serviceReq.serviceCatalogCode());
                    }

                    // 3.4 Tạo dòng dịch vụ
                    OrderServiceDtl osd = new OrderServiceDtl();
                    osd.setCode(generateOrderServiceDtlCode());
                    osd.setOrderDetail(detail);
                    osd.setServiceCatalogCode(serviceReq.serviceCatalogCode());

                    // 3.5 Kiểm tra giá dòng dịch vụ khi dùng combo
                    BigDecimal linePrice = Optional.ofNullable(serviceReq.adjustedPrice()).orElse(BigDecimal.ZERO);
                    if (linePrice.compareTo(BigDecimal.ZERO) > 0) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "orderType=USE_COMBO thì giá dịch vụ phải = 0 (đã trả bằng combo)");
                    }
                    if (Boolean.TRUE.equals(serviceReq.adjustedPriceFlag())) {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(true);
                        osd.setAdjustedPriceReason(serviceReq.adjustedPriceReason());
                    } else {
                        osd.setAdjustedPrice(serviceReq.adjustedPrice());
                        osd.setAdjustedPriceFlag(false);
                        osd.setAdjustedPriceReason(null);
                    }

                    // 3.6 Gắn combo vào dòng dịch vụ (để trace)
                    osd.setServiceComboCatalogCode(comboCardCode);

                    // 3.7 Validate + lock + trừ quota
                    useComboOrThrow(comboCardCode, serviceReq.serviceCatalogCode());

                    // 3.8 Tăng used_uses summary
                    customerComboSummaryRepository.increaseUsedUses(comboCardCode, 1);

                    // 3.9 Nếu hết quota → set EXHAUSTED
                    if (!customerComboQuotaRepository.existsRemaining(comboCardCode)) {
                        customerComboSummaryRepository.setStatus(comboCardCode, "EXHAUSTED");
                    }

                    // 3.10 Lưu dòng dịch vụ
                    orderServiceDtlRepository.save(osd);
                }
            }
        }

        // ===== 5. Lưu totalPrice (FE tính) =====
        order.setTotalPrice(request.totalPrice());
        orderRepository.save(order);

        // ===== 6. Promotion (giữ nguyên, chỉ ghi nhận lượt dùng) =====
        if (request.promotionId() != null) {

            if (customerId == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Chưa chọn khách hàng nên không áp dụng khuyến mãi được");
            }

            Promotion promo = promotionRepository.findById(UUID.fromString(request.promotionId())).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Promotion không tồn tại"));

            validatePromotionActive(promo);
            validateCustomerEligible(customerId, promo);

            CustomerPromotion cp = new CustomerPromotion();
            cp.setCustomerId(customerId);
            cp.setPromotionId(promo.getId());
            cp.setOrderCode(order.getCode());
            cp.setUsedAt(LocalDateTime.now());

            // tạm thời chỉ log lượt dùng
            cp.setDiscountAmount(BigDecimal.ZERO);
            cp.setDiscountPercent(BigDecimal.ZERO);

            customerPromotionRepository.save(cp);
        }

        return order.getId();
    }

    /**
     * Hàm xử lý update tạo mới
     *
     * @param customer
     * @param licensePlate
     * @param brandCode
     * @param modelCode
     * @param imageUrl
     * @param vehicleNote
     * @return
     */
    private Vehicle handleVehicleOnOrder(Customer customer, String licensePlate, String brandCode, String modelCode, String imageUrl, String vehicleNote) {
        // 2.1 Kiểm tra input
        if (StringUtils.isNullOrBlank(licensePlate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        String plate = licensePlate.trim();
        // 2.2 Kiểm tra xem xe này có tồn tại chưa
        Vehicle vehicle = vehicleRepository.findByLicensePlate(plate).orElse(null);
        if (ObjectUtils.isNull(vehicle)) {
            // 2.2.1 Nếu chưa thì tạo mới (bắt buộc có brandCode + modelCode)
            if (StringUtils.isNullOrBlank(brandCode) || StringUtils.isNullOrBlank(modelCode)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Thiếu thông tin hãng xe hoặc dòng xe");
            }

            Brand brand = brandRepository.findByCode(brandCode.trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại: " + brandCode));

            Model model = modelRepository.findByCode(modelCode.trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại: " + modelCode));

            // check model thuộc brand
            if (model.getBrand() == null || !model.getBrand().getCode().equals(brand.getCode())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Model không thuộc brand đã chọn");
            }

            vehicle = Vehicle.builder().licensePlate(plate).brand(brand).model(model).customer(customer).imageUrl(imageUrl).note(vehicleNote).build();

            vehicle = vehicleRepository.save(vehicle);

            return vehicle;

        } else {
            // 2.2.2 Nếu rồi thì xem có cập nhật trường nào không (nếu có thì cập nhật)
            boolean changed = false;

            // customer: nếu request có customerId thì cập nhật theo customer hiện tại
            if (customer != null && (vehicle.getCustomer() == null || !vehicle.getCustomer().getId().equals(customer.getId()))) {
                vehicle.setCustomer(customer);
                changed = true;
            }

            // brand/model: chỉ update khi request truyền đủ brandCode + modelCode
            boolean hasBrandModel = !StringUtils.isNullOrBlank(brandCode) && !StringUtils.isNullOrBlank(modelCode);

            if (hasBrandModel) {
                Brand brand = brandRepository.findByCode(brandCode.trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại: " + brandCode));

                Model model = modelRepository.findByCode(modelCode.trim()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại: " + modelCode));

                if (model.getBrand() == null || !model.getBrand().getCode().equals(brand.getCode())) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Model không thuộc brand đã chọn");
                }

                if (vehicle.getBrand() == null || !vehicle.getBrand().getCode().equals(brand.getCode())) {
                    vehicle.setBrand(brand);
                    changed = true;
                }
                if (vehicle.getModel() == null || !vehicle.getModel().getCode().equals(model.getCode())) {
                    vehicle.setModel(model);
                    changed = true;
                }
            }

            // imageUrl: chỉ update nếu request gửi lên (khác null) và khác giá trị cũ
            if (imageUrl != null && !imageUrl.equals(vehicle.getImageUrl())) {
                vehicle.setImageUrl(imageUrl);
                changed = true;
            }

            // note: chỉ update nếu request gửi lên (khác null) và khác giá trị cũ
            if (vehicleNote != null && !vehicleNote.equals(vehicle.getNote())) {
                vehicle.setNote(vehicleNote);
                changed = true;
            }

            if (changed) {
                vehicle = vehicleRepository.save(vehicle);
                return vehicle;
            }
            return vehicle;
        }
    }


    private void createCustomerComboQuota(CustomerComboSummary ccs) {
        // 1. Lấy danh sách cấu hình quota (quality) của combo
        //    Query theo comboCode (service_combo.code), KHÔNG phải combo_catalog_code
        List<ServiceComboQuality> lines = serviceComboQualityRepository.findByComboCatalogCode(ccs.getServiceComboCatalogCode());

        // 2. Kiểm tra combo template đã được cấu hình quota hay chưa
        //    Nếu chưa có cấu hình thì không thể tạo quota cho khách hàng
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Combo template chưa có cấu hình quality: " + ccs.getServiceComboCatalogCode());
        }
        // 3. Duyệt từng dòng cấu hình quota của combo
        for (ServiceComboQuality line : lines) {
            // 4. Lấy thông tin ServiceCatalog tương ứng với quota
            //    Nếu entity ServiceComboQuality đã map sẵn ServiceCatalog thì dùng luôn
            ServiceCatalog sc = line.getServiceCatalog();
            // 5. Trường hợp ServiceCatalog chưa được load (null)
            //    thì query lại theo code để đảm bảo dữ liệu đầy đủ
            if (ObjectUtils.isNull(sc)) {
                sc = serviceCatalogRepository.findByCode(line.getComboCatalog().getCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "ServiceCatalog không tồn tại: " + line.getServiceCatalog().getCode()));
            }
            // 6. Khởi tạo quota cho khách hàng theo từng dịch vụ trong combo
            CustomerComboQuota quota = CustomerComboQuota.builder()
                    // 6.1 Sinh mã quota cho khách hàng
                    .code(generateCustomerComboQuotaCode())
                    // 6.2 Gắn quota này thuộc về combo summary nào
                    .customerComboSummary(ccs)
                    // 6.3 Gắn quota cho dịch vụ cụ thể
                    .serviceCatalog(sc)
                    // 6.4 Tổng số lượt được phép sử dụng của dịch vụ này
                    .totalUses(line.getQuality())
                    // 6.5 Số lượt đã sử dụng ban đầu = 0
                    .usedUses(0)
                    // 6.6 Thời điểm hết hạn quota (theo combo summary)
                    .endAt(ccs.getEndAt()).build();
            // 7. Lưu quota của khách hàng vào database
            customerComboQuotaRepository.save(quota);
        }
    }

    private String generateCustomerComboQuotaCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        long millis = System.currentTimeMillis() % 1_000_000; // lấy phần đuôi
        String timePart = Long.toString(millis, 36).toUpperCase();
        String random = Integer.toString(ThreadLocalRandom.current().nextInt(36 * 36), 36).toUpperCase();
        return "CCQ-" + date + "-" + timePart + random;
    }

    /**
     * Tạo customer combo để quản lý số lượt sử dụng còn lại của mỗi hóa đơn
     *
     * @param order
     * @param customer
     * @param comboCatalog
     * @return
     */
    private CustomerComboSummary createCustomerComboCard(Order order, Customer customer, ServiceComboCatalog comboCatalog, BigDecimal soldPrice, String soldPriceReason, Boolean soldPriceFlag) {
        // 1. Lấy thông tin combo gốc (thời hạn, cấu hình nghiệp vụ) theo comboCode từ catalog
        ServiceCombo combo = serviceComboRepository.findByCode(comboCatalog.getComboCode());
        // 2. Lấy số ngày hiệu lực của combo, nếu chưa cấu hình thì mặc định = 0
        int durationDays = combo.getDurationDays() == null ? 0 : combo.getDurationDays();
        // 3. Xác định thời điểm bắt đầu hiệu lực combo (thời điểm mua)
        LocalDateTime startAt = LocalDateTime.now();
        // 4. Tính thời điểm hết hạn combo dựa trên số ngày hiệu lực
        LocalDateTime endAt = startAt.plusDays(durationDays);
        // 5. Tính tổng số lượt sử dụng mà combo cho phép
        //    (tổng quantity của các dịch vụ cấu hình trong combo)
        Integer totalUsesObj = serviceComboRepository.sumQuantityByComboCatalogCode(comboCatalog.getComboCode());
        // 6. Xử lý null an toàn khi combo chưa cấu hình dịch vụ
        int totalUses = totalUsesObj == null ? 0 : totalUsesObj;
        // 7. Khởi tạo thẻ combo cho khách hàng (CustomerComboSummary)
        CustomerComboSummary ccs = CustomerComboSummary.builder()
                // 7.1 Sinh mã combo riêng cho khách hàng
                .code(generateCustomerComboCode())
                // 7.2 Gắn combo này cho khách hàng
                .customer(customer)
                // 7.3 Gắn combo được mua từ đơn hàng nào
                .orderCode(order.getCode())
                // 7.4 Lưu code của combo (business key)
                .serviceComboCatalogCode(comboCatalog.getCode())
                // 7.5 Thời gian bắt đầu hiệu lực combo
                .startAt(startAt)
                // 7.6 Thời gian hết hạn combo
                .endAt(endAt)
                // 7.7 Tổng số lượt combo được phép sử dụng
                .totalUses(totalUses)
                // 7.8 Số lượt đã sử dụng ban đầu = 0
                .usedUses(0)
                // 7.9 Trạng thái ban đầu của combo
                .status("ACTIVE").build();
        // 8. Lưu thẻ combo của khách hàng vào database và trả về kết quả
        return customerComboSummaryRepository.save(ccs);
    }

    private String generateCustomerComboCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        long millis = System.currentTimeMillis() % 1_000_000; // lấy phần đuôi
        String timePart = Long.toString(millis, 36).toUpperCase();
        String random = Integer.toString(ThreadLocalRandom.current().nextInt(36 * 36), 36).toUpperCase();
        return "CC-" + date + "-" + timePart + random;
    }

    /**
     * Dùng combo cho 1 dịch vụ:
     * - Check combo tồn tại + ACTIVE
     * - Check chưa hết hạn
     * - Lock quota theo (comboCardCode + serviceCatalogCode) để tránh trừ trùng
     * - Check còn lượt
     * - Trừ 1 lượt và lưu
     */
    private void useComboOrThrow(String comboCardCode, String serviceCatalogCode) {

        // 1) Lấy combo card ACTIVE
        CustomerComboSummary ccs = customerComboSummaryRepository.findActiveByCode(comboCardCode).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Combo không tồn tại hoặc không ACTIVE: " + comboCardCode));

        // 2) Check hết hạn
        if (LocalDateTime.now().isAfter(ccs.getEndAt())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Combo đã hết hạn: " + comboCardCode);
        }

        // 3) Lock quota theo combo + service để tránh 2 người trừ cùng lúc
        CustomerComboQuota quota = customerComboQuotaRepository.lockByComboAndService(comboCardCode, serviceCatalogCode).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Combo không có quota cho dịch vụ: " + serviceCatalogCode));

        // 4) Tính lượt còn lại
        int used = quota.getUsedUses() == null ? 0 : quota.getUsedUses();
        int remaining = quota.getTotalUses() - used;

        // 5) Hết lượt → chặn
        if (remaining <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Dịch vụ đã hết lượt trong combo: " + serviceCatalogCode);
        }

        // 6) Trừ 1 lượt
        quota.setUsedUses(used + 1);
        customerComboQuotaRepository.save(quota);
    }

    /**
     * Chuẩn hóa mã combo card (thẻ combo khách hàng) dùng cho dịch vụ.
     * raw có thể là:
     * - null/blank          : không dùng combo
     * - "USE_NEW_COMBO"     : dùng thẻ combo vừa tạo trong cùng request (created)
     * - "CC..."             : dùng thẻ combo có sẵn
     * - "CB..."             : sai (template code) → throw
     */
    private String normalizeComboCardCode(String raw, CustomerComboSummary created) {

        // 1) Không truyền → không dùng combo
        if (StringUtils.isNullOrBlank(raw)) return null;

        // 2) Chuẩn hóa input
        String v = raw.trim().toUpperCase();

        // 3) Dùng combo vừa tạo mới trong request
        if ("USE_NEW_COMBO".equals(v)) {
            if (created == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "USE_NEW_COMBO nhưng request không có orderType=COMBO để tạo thẻ combo trước");
            }
            return created.getCode(); // trả về CCxxx vừa tạo
        }

        // 4) Thẻ combo khách hàng hợp lệ: CC...
        if (v.startsWith("CC")) return v;

        // 5) FE truyền nhầm template (CB...) ở luồng dùng combo → sai nghĩa
        if (v.startsWith("CB")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Dùng combo phải truyền mã thẻ combo (CCxxx), không phải template (CBxxx)");
        }

        // 6) Format khác → coi như không dùng combo
        return null;
    }


    private void validatePromotionActive(Promotion p) {
        if (Boolean.TRUE.equals(p.getDeleteFlag())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Promotion đã bị xóa");
        }
        if (p.getStatus() != PromotionStatus.ACTIVE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Promotion chưa được kích hoạt");
        }
        LocalDateTime now = LocalDateTime.now();
        if (p.getStartDate() != null && p.getStartDate().isAfter(now)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Promotion chưa đến ngày bắt đầu");
        }
        if (p.getEndDate() != null && p.getEndDate().isBefore(now)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Promotion đã hết hạn");
        }
    }

    private void validateCustomerEligible(UUID customerId, Promotion promo) {

        // Rule khách cũ: tạm thời dựa vào targetAudience
        if (requiresOldCustomer(promo)) {
            boolean isOldCustomer = orderRepository.isOldCustomer(customerId, promo.getStartDate());
            if (!isOldCustomer) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Khách không phải khách cũ");
            }
        }

        // usageLimit (0/null = vô hạn)
        int limit = promo.getUsageLimit() == null ? 0 : promo.getUsageLimit();
        if (limit > 0) {
            long used = customerPromotionRepository.countUsed(customerId, promo.getId());
            if (used >= limit) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Đã vượt quá số lần sử dụng chương trình");
            }
        }
    }

    private boolean requiresOldCustomer(Promotion p) {
        String audience = p.getTargetAudience() == null ? "" : p.getTargetAudience().trim().toLowerCase();
        return audience.contains("khách cũ") || audience.contains("khach cu") || audience.contains("old_customer") || audience.contains("loyal");
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
        Order order = orderRepository.findById(request.orderId()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Đơn hàng không tồn tại"));

        // 2. Lấy khách hàng nếu có
        Customer customer = null;
        UUID customerId = request.customerId();
        if (customerId != null && !StringUtils.isUUIDNullOrBlank(customerId)) {
            customer = customerRepository.findById(customerId).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }
        order.setCustomer(customer);

        // 3. Lấy hoặc tạo mới xe
        if (StringUtils.isNullOrBlank(request.licensePlate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.licensePlate()).orElse(null);
        if (ObjectUtils.isNull(vehicle)) {
            if (StringUtils.isNullOrBlank(request.brandCode()) || StringUtils.isNullOrBlank(request.modelCode())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Thiếu thông tin hãng hoặc dòng xe khi tạo mới xe");
            }

            Brand brand = brandRepository.findByCode(request.brandCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Hãng xe không tồn tại"));
            Model model = modelRepository.findByCode(request.modelCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Dòng xe không tồn tại"));

            vehicle = Vehicle.builder().licensePlate(request.licensePlate()).brand(brand).model(model).customer(customer).build();
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
        ObjectUtils.setIfNotNull(request.date(), order::setDate);
        ObjectUtils.setIfNotNull(request.vat(), order::setVat);
        ObjectUtils.setIfNotNull(request.tip(), order::setTip);
        ObjectUtils.setIfNotNull(request.discount(), order::setDiscount);
        ObjectUtils.setIfNotNull(request.note(), order::setNote);

        orderRepository.save(order);

        BigDecimal totalServicePrice = BigDecimal.ZERO;

        // 5. Duyệt từng order detail
        for (OrderUpdateRequest.OrderDetailUpdateRequest detailReq : request.orderDetails()) {
            OrderDetail detail = orderDetailRepository.findByCode(detailReq.orderDetailCode()).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Chi tiết đơn hàng không tồn tại: " + detailReq.orderDetailCode()));

            detail.setVehicle(vehicle); // Cập nhật lại xe

            ObjectUtils.setIfNotNull(detailReq.status(), detail::setStatus);
            ObjectUtils.setIfNotNull(detailReq.note(), detail::setNote);

            if (detailReq.employeeIds() != null && !detailReq.employeeIds().isEmpty()) {
                String employeeStr = detailReq.employeeIds().stream().map(String::valueOf).collect(Collectors.joining(","));
                detail.setEmployeeId(employeeStr);
            }

            orderDetailRepository.save(detail);

            // 6. Cập nhật dịch vụ
            if (CollectionUtils.isNotEmpty(detailReq.services())) {
                List<OrderServiceDtl> existingServices = orderServiceDtlRepository.findByOrderDetailCode(detail.getCode());

                Map<String, OrderServiceDtl> existingMap = existingServices.stream().collect(Collectors.toMap(OrderServiceDtl::getServiceCatalogCode, s -> s));

                Set<String> requestScCodes = detailReq.services().stream().map(OrderUpdateRequest.ServiceUpdateRequest::serviceCatalogCode).collect(Collectors.toSet());

                // 1. Xoá service không còn trong request
                Set<String> codesToRemove = new HashSet<>(existingMap.keySet());
                codesToRemove.removeAll(requestScCodes);
                if (!codesToRemove.isEmpty()) {
                    orderServiceDtlRepository.deleteByOrderDetailCodeAndServiceCatalogCodes(detail.getCode(), codesToRemove);
                }

                // 2. Thêm hoặc cập nhật service trong request
                for (OrderUpdateRequest.ServiceUpdateRequest serviceReq : detailReq.services()) {
                    OrderServiceDtl osd = existingMap.get(serviceReq.serviceCatalogCode());

                    if (osd == null) {
                        // Thêm mới
                        osd = OrderServiceDtl.builder().code(generateOrderServiceDtlCode()).orderDetail(detail).serviceCatalogCode(serviceReq.serviceCatalogCode()).build();
                    }

                    // Cập nhật thông tin giá
                    osd.setAdjustedPrice(serviceReq.adjustedPrice());
                    osd.setAdjustedPriceFlag(Boolean.TRUE.equals(serviceReq.adjustedPriceFlag()));
                    osd.setAdjustedPriceReason(Boolean.TRUE.equals(serviceReq.adjustedPriceFlag()) ? serviceReq.adjustedPriceReason() : null);

                    orderServiceDtlRepository.save(osd);

                    // Luôn cộng adjustedPrice (FE đã gửi giá gốc hoặc giá điều chỉnh)
                    if (serviceReq.adjustedPrice() != null) {
                        totalServicePrice = totalServicePrice.add(serviceReq.adjustedPrice());
                    }
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
