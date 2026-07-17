package com.alphawash.service.impl;

import com.alphawash.constant.CustomerStatus;
import com.alphawash.constant.RegexConstant;
import com.alphawash.converter.CustomerConverter;
import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.CustomerSegmentDto.SegmentBadge;
import com.alphawash.dto.CustomerVehicleDto;
import com.alphawash.dto.CustomerVehicleFlatDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.CustomerSegment;
import com.alphawash.entity.Model;
import com.alphawash.entity.Order;
import com.alphawash.entity.OrderDetail;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.exception.DuplicateVehicleException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.CustomerListRepository;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.CustomerSegmentMembershipRepository;
import com.alphawash.repository.CustomerSegmentRepository;
import com.alphawash.repository.CustomerStatsRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.CustomerCreateRequest;
import com.alphawash.request.CustomerUpdateRequest;
import com.alphawash.request.CustomerVehicleRequest;
import com.alphawash.response.CustomerDetailResponse;
import com.alphawash.response.CustomerInvoiceRowResponse;
import com.alphawash.response.CustomerListItemResponse;
import com.alphawash.response.CustomerStatsResponse;
import com.alphawash.response.CustomerStatsResponse.ServiceBreakdownResponse;
import com.alphawash.response.CustomerStatsResponse.SpendingMonthResponse;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.response.PageResponse;
import com.alphawash.response.VehicleRefResponse;
import com.alphawash.response.VehicleSummaryResponse;
import com.alphawash.service.CustomerService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.LicensePlateUtil;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerSegmentRepository customerSegmentRepository;
    private final CustomerSegmentMembershipRepository customerSegmentMembershipRepository;
    private final CustomerListRepository customerListRepository;
    private final CustomerStatsRepository customerStatsRepository;

    @Override
    public List<CustomerDto> getAll() {
        return CustomerConverter.INSTANCE.toDto(customerRepository.findAll());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        customerRepository
                .findById(id)
                .ifPresentOrElse(
                        customer -> {
                            customer.setDeleteFlag(true);
                            customerRepository.save(customer);
                        },
                        () -> {
                            throw new BusinessException(
                                    HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng để xóa với ID: " + id);
                        });
    }

    @Override
    public List<CustomerVehicleResponse> findCustomerVehicleByPhoneOrLicensePlate(String phoneOrLicensePlate) {
        if (StringUtils.isNullOrBlank(phoneOrLicensePlate)) {
            return Collections.emptyList();
        }

        String searchTerm = phoneOrLicensePlate.trim().toUpperCase();
        List<CustomerVehicleFlatDto> flatList;

        if (searchTerm.matches(RegexConstant.PHONE_REGEX)) {
            flatList = customerRepository.findCustomerWithVehicleByPhone(searchTerm);
        } else if (searchTerm.matches(RegexConstant.LICENSE_PLATE_REGEX)) {
            flatList = customerRepository.findCustomerWithVehicleByLicensePlate(searchTerm);
        } else if (searchTerm.startsWith("0") && searchTerm.matches("^0\\d*$")) {
            flatList = customerRepository.findCustomerWithVehicleByPhoneLike(searchTerm);
        } else if (searchTerm.matches("^\\d.*[A-Z].*") || searchTerm.matches("^\\d{1,2}[A-Z].*")) {
            flatList = customerRepository.findCustomerWithVehicleByLicensePlateLike(searchTerm + "%");
        } else {
            List<CustomerVehicleFlatDto> phoneResults =
                    customerRepository.findCustomerWithVehicleByPhoneLike(searchTerm + "%");
            List<CustomerVehicleFlatDto> plateResults =
                    customerRepository.findCustomerWithVehicleByLicensePlateLike(searchTerm + "%");

            flatList = new ArrayList<>();
            flatList.addAll(phoneResults);
            flatList.addAll(plateResults);
            flatList = new ArrayList<>(flatList.stream()
                    .collect(Collectors.toMap(
                            dto -> dto.getId() + "_" + dto.getLicensePlate(),
                            dto -> dto,
                            (existing, replacement) -> existing))
                    .values());
        }

        if (CollectionUtils.isEmpty(flatList)) {
            return Collections.emptyList();
        }

        Map<UUID, List<CustomerVehicleFlatDto>> groupedByCustomer =
                flatList.stream().collect(Collectors.groupingBy(CustomerVehicleFlatDto::getId));

        return groupedByCustomer.values().stream()
                .map(customerData -> {
                    CustomerVehicleFlatDto first = customerData.get(0);

                    List<CustomerVehicleDto> vehicles = customerData.stream()
                            .filter(item -> ObjectUtils.isNotNull(item.getLicensePlate()))
                            .map(flat -> new CustomerVehicleDto(
                                    flat.getBrandCode(),
                                    flat.getBrandName(),
                                    flat.getModelCode(),
                                    flat.getModelName(),
                                    flat.getLicensePlate(),
                                    flat.getVehicleId(),
                                    flat.getSize(),
                                    flat.getImageUrl(),
                                    first.getId()))
                            .distinct()
                            .collect(Collectors.toList());

                    return CustomerVehicleResponse.builder()
                            .id(first.getId())
                            .name(first.getCustomerName())
                            .phone(first.getPhone())
                            .vehicles(vehicles)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CustomerListItemResponse> list(CustomerListRepository.Criteria criteria, int page, int size) {
        long total = customerListRepository.count(criteria);
        List<CustomerListRepository.Row> rows =
                total == 0 ? List.of() : customerListRepository.search(criteria, page, size);
        List<CustomerListItemResponse> content = enrichRows(rows);
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return PageResponse.<CustomerListItemResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public CustomerDetailResponse getDetail(UUID id) {
        Customer customer = customerRepository
                .findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getDeleteFlag()))
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + id));

        List<VehicleSummaryResponse> vehicles = vehicleRepository.findByCustomerIdAndDeleteFlagFalse(id).stream()
                .map(this::toVehicleSummary)
                .toList();

        List<SegmentBadge> tags = computeTagsForCustomers(List.of(id)).getOrDefault(id, List.of());
        CustomerStatsResponse stats = computeStats(id);

        return CustomerDetailResponse.builder()
                .id(customer.getId())
                .name(customer.getCustomerName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .gender(customer.getGender())
                .birthday(customer.getBirthday())
                .address(customer.getAddress())
                .note(customer.getNote())
                .avatarUrl(customer.getAvatarUrl())
                .status(customer.getStatus() != null ? customer.getStatus() : CustomerStatus.ACTIVE)
                .createdAt(customer.getCreatedAt())
                .vehicles(vehicles)
                .tags(tags)
                .stats(stats)
                .build();
    }

    @Override
    public PageResponse<CustomerInvoiceRowResponse> getInvoices(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var ordersPage = orderRepository.findByCustomer_IdAndDeleteFlagFalseOrderByDateDesc(id, pageable);

        List<String> codes = ordersPage.getContent().stream().map(Order::getCode).toList();
        List<OrderDetail> details =
                codes.isEmpty() ? List.of() : orderDetailRepository.findByOrder_CodeIn(codes);

        Map<String, String> plateByOrderCode = details.stream()
                .filter(d -> d.getOrder() != null && d.getVehicle() != null)
                .collect(Collectors.toMap(
                        d -> d.getOrder().getCode(), d -> d.getVehicle().getLicensePlate(), (a, b) -> a));

        List<CustomerInvoiceRowResponse> content = ordersPage.getContent().stream()
                .map(o -> CustomerInvoiceRowResponse.builder()
                        .id(o.getId())
                        .code(o.getCode())
                        .date(o.getDate())
                        .totalPrice(o.getTotalPrice())
                        .paymentStatus(o.getPaymentStatus())
                        .paymentType(o.getPaymentType())
                        .licensePlate(plateByOrderCode.get(o.getCode()))
                        .build())
                .toList();

        return PageResponse.<CustomerInvoiceRowResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(ordersPage.getTotalElements())
                .totalPages(ordersPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public CustomerDetailResponse createCustomer(CustomerCreateRequest request) {
        if (StringUtils.isNullOrBlank(request.name())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tên khách hàng không được để trống");
        }
        if (StringUtils.isNullOrBlank(request.phone())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        customerRepository.findByPhone(request.phone().trim()).ifPresent(existing -> {
            throw new BusinessException(
                    HttpStatus.CONFLICT, "Khách hàng đã tồn tại trong hệ thống: " + request.phone());
        });

        Customer customer = Customer.builder()
                .customerName(request.name().trim())
                .phone(request.phone().trim())
                .email(StringUtils.isNullOrBlank(request.email()) ? null : request.email().trim())
                .gender(request.gender())
                .birthday(request.birthday())
                .address(StringUtils.isNullOrBlank(request.address()) ? null : request.address().trim())
                .note(request.note())
                .status(CustomerStatus.ACTIVE)
                .build();
        customer = customerRepository.save(customer);

        if (request.vehicle() != null) {
            attachVehicle(customer, request.vehicle());
        }

        return getDetail(customer.getId());
    }

    @Override
    @Transactional
    public CustomerDetailResponse updateCustomer(UUID id, CustomerUpdateRequest request) {
        Customer existing = customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + id));

        if (StringUtils.isNotNullOrBlank(request.phone()) && !request.phone().trim().equals(existing.getPhone())) {
            customerRepository.findByPhone(request.phone().trim()).ifPresent(duplicate -> {
                if (!duplicate.getId().equals(id)) {
                    throw new BusinessException(
                            HttpStatus.CONFLICT, "Số điện thoại đã tồn tại: " + request.phone());
                }
            });
            existing.setPhone(request.phone().trim());
        }

        if (StringUtils.isNotNullOrBlank(request.name())) {
            existing.setCustomerName(request.name().trim());
        }
        if (request.email() != null) {
            existing.setEmail(request.email().isBlank() ? null : request.email().trim());
        }
        if (request.gender() != null) {
            existing.setGender(request.gender());
        }
        if (request.birthday() != null) {
            existing.setBirthday(request.birthday());
        }
        if (request.address() != null) {
            existing.setAddress(request.address().isBlank() ? null : request.address().trim());
        }
        if (request.note() != null) {
            existing.setNote(request.note());
        }
        if (request.status() != null) {
            existing.setStatus(request.status());
        }

        customerRepository.save(existing);
        return getDetail(id);
    }

    @Override
    @Transactional
    public VehicleSummaryResponse addVehicle(UUID customerId, CustomerVehicleRequest request) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + customerId));

        Vehicle vehicle = attachVehicle(customer, request);
        return toVehicleSummary(vehicle);
    }

    @Override
    @Transactional
    public VehicleSummaryResponse updateVehicle(UUID customerId, UUID vehicleId, CustomerVehicleRequest request) {
        Vehicle vehicle = vehicleRepository
                .findById(vehicleId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + vehicleId));

        if (vehicle.getCustomer() == null || !vehicle.getCustomer().getId().equals(customerId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Xe không thuộc về khách hàng này");
        }

        if (StringUtils.isNotNullOrBlank(request.licensePlate())) {
            String normalized = LicensePlateUtil.normalize(request.licensePlate());
            Vehicle finalVehicle = vehicle;
            vehicleRepository
                    .findByNormalizedLicensePlateAndDeleteFlagFalse(normalized)
                    .filter(other -> !other.getId().equals(finalVehicle.getId()))
                    .ifPresent(other -> {
                        throw new DuplicateVehicleException(
                                "Biển số xe đã tồn tại: " + other.getLicensePlate(),
                                VehicleConverter.INSTANCE.toDto(other));
                    });
            vehicle.setLicensePlate(request.licensePlate().trim());
            vehicle.setNormalizedLicensePlate(normalized);
        }

        applyBrandAndModel(vehicle, request);
        if (request.note() != null) {
            vehicle.setNote(request.note());
        }

        vehicle = vehicleRepository.save(vehicle);
        return toVehicleSummary(vehicle);
    }

    @Override
    @Transactional
    public void removeVehicle(UUID customerId, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository
                .findById(vehicleId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + vehicleId));

        if (vehicle.getCustomer() == null || !vehicle.getCustomer().getId().equals(customerId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Xe không thuộc về khách hàng này");
        }

        vehicle.setCustomer(null);
        vehicleRepository.save(vehicle);
    }

    @Override
    public CustomerStatsResponse recalculateStats(UUID id) {
        customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + id));
        return computeStats(id);
    }

    @Override
    public byte[] exportCsv(CustomerListRepository.Criteria criteria, List<UUID> ids) {
        List<CustomerListItemResponse> items;
        if (!CollectionUtils.isEmpty(ids)) {
            items = ids.stream().map(this::buildListItemForSingleCustomer).filter(Objects::nonNull).toList();
        } else {
            long total = customerListRepository.count(criteria);
            int exportSize = (int) Math.min(Math.max(total, 0), 20_000);
            List<CustomerListRepository.Row> rows =
                    exportSize == 0 ? List.of() : customerListRepository.search(criteria, 0, exportSize);
            items = enrichRows(rows);
        }
        return buildCsv(items);
    }

    // --- Helpers ---

    private List<CustomerListItemResponse> enrichRows(List<CustomerListRepository.Row> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = rows.stream().map(CustomerListRepository.Row::id).toList();
        Map<UUID, List<VehicleRefResponse>> vehiclesByCustomer = computeVehicleRefsForCustomers(ids);
        Map<UUID, List<SegmentBadge>> tagsByCustomer = computeTagsForCustomers(ids);

        return rows.stream()
                .map(r -> CustomerListItemResponse.builder()
                        .id(r.id())
                        .name(r.customerName())
                        .phone(r.phone())
                        .email(r.email())
                        .avatarUrl(r.avatarUrl())
                        .vehicles(vehiclesByCustomer.getOrDefault(r.id(), List.of()))
                        .vehicleCount(r.vehicleCount())
                        .totalVisits(r.totalVisits())
                        .totalSpending(r.totalSpending())
                        .lastVisitDate(r.lastVisitDate())
                        .tags(tagsByCustomer.getOrDefault(r.id(), List.of()))
                        .status(r.status())
                        .createdAt(r.createdAt())
                        .build())
                .toList();
    }

    private CustomerListItemResponse buildListItemForSingleCustomer(UUID id) {
        return customerRepository
                .findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getDeleteFlag()))
                .map(c -> {
                    var agg = customerStatsRepository.computeAggregateStats(id);
                    List<VehicleRefResponse> vehicles = vehicleRepository.findByCustomerIdAndDeleteFlagFalse(id)
                            .stream()
                            .map(v -> new VehicleRefResponse(v.getId(), v.getLicensePlate()))
                            .toList();
                    List<SegmentBadge> tags = computeTagsForCustomers(List.of(id)).getOrDefault(id, List.of());
                    return CustomerListItemResponse.builder()
                            .id(c.getId())
                            .name(c.getCustomerName())
                            .phone(c.getPhone())
                            .email(c.getEmail())
                            .avatarUrl(c.getAvatarUrl())
                            .vehicles(vehicles)
                            .vehicleCount(vehicles.size())
                            .totalVisits(agg.totalVisits())
                            .totalSpending(agg.totalSpending())
                            .lastVisitDate(agg.lastVisitDate())
                            .tags(tags)
                            .status(c.getStatus() != null ? c.getStatus().name() : CustomerStatus.ACTIVE.name())
                            .createdAt(c.getCreatedAt())
                            .build();
                })
                .orElse(null);
    }

    private CustomerStatsResponse computeStats(UUID customerId) {
        var agg = customerStatsRepository.computeAggregateStats(customerId);
        var monthly = customerStatsRepository.computeSpendingByMonth(customerId, 6);
        var breakdown = customerStatsRepository.computeServiceBreakdown(customerId);

        return CustomerStatsResponse.builder()
                .totalVisits(agg.totalVisits())
                .totalSpending(agg.totalSpending())
                .avgInvoice(agg.avgInvoice())
                .firstVisitDate(agg.firstVisitDate())
                .lastVisitDate(agg.lastVisitDate())
                .spendingByMonth(monthly.stream()
                        .map(m -> SpendingMonthResponse.builder()
                                .month(m.month())
                                .total(m.total())
                                .build())
                        .toList())
                .serviceBreakdown(breakdown.stream()
                        .map(b -> ServiceBreakdownResponse.builder()
                                .serviceName(b.serviceName())
                                .count(b.count())
                                .total(b.total())
                                .build())
                        .toList())
                .build();
    }

    private Map<UUID, List<VehicleRefResponse>> computeVehicleRefsForCustomers(List<UUID> customerIds) {
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, List<VehicleRefResponse>> result = new HashMap<>();
        for (Vehicle v : vehicleRepository.findByCustomerIdInAndDeleteFlagFalse(customerIds)) {
            if (v.getCustomer() == null) {
                continue;
            }
            result.computeIfAbsent(v.getCustomer().getId(), k -> new ArrayList<>())
                    .add(new VehicleRefResponse(v.getId(), v.getLicensePlate()));
        }
        return result;
    }

    private Map<UUID, List<SegmentBadge>> computeTagsForCustomers(List<UUID> customerIds) {
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        List<CustomerSegment> segments = customerSegmentRepository.findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();
        Map<String, SegmentBadge> badgeByCode = segments.stream()
                .collect(Collectors.toMap(
                        CustomerSegment::getCode,
                        s -> SegmentBadge.builder()
                                .code(s.getCode())
                                .segmentName(s.getSegmentName())
                                .color(s.getColor())
                                .icon(s.getIcon())
                                .build()));

        Map<UUID, List<SegmentBadge>> result = new HashMap<>();
        for (var membership : customerSegmentMembershipRepository.findByCustomerIdIn(customerIds)) {
            SegmentBadge badge = badgeByCode.get(membership.getSegmentCode());
            if (badge != null) {
                result.computeIfAbsent(membership.getCustomerId(), k -> new ArrayList<>())
                        .add(badge);
            }
        }
        return result;
    }

    private VehicleSummaryResponse toVehicleSummary(Vehicle v) {
        Brand brand = v.getBrand();
        Model model = v.getModel();
        return VehicleSummaryResponse.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .brandId(brand != null ? brand.getId() : null)
                .brandCode(brand != null ? brand.getCode() : null)
                .brandName(brand != null ? brand.getBrandName() : null)
                .modelId(model != null ? model.getId() : null)
                .modelCode(model != null ? model.getCode() : null)
                .modelName(model != null ? model.getModelName() : null)
                .size(model != null && model.getSize() != null ? model.getSize().name() : null)
                .imageUrl(v.getImageUrl())
                .customerId(v.getCustomer() != null ? v.getCustomer().getId() : null)
                .build();
    }

    private Vehicle attachVehicle(Customer customer, CustomerVehicleRequest request) {
        if (request.linkVehicleId() != null) {
            Vehicle vehicle = vehicleRepository
                    .findById(request.linkVehicleId())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + request.linkVehicleId()));
            if (vehicle.getCustomer() != null
                    && !vehicle.getCustomer().getId().equals(customer.getId())) {
                throw new DuplicateVehicleException(
                        "Xe đã có chủ sở hữu khác: " + vehicle.getLicensePlate(),
                        VehicleConverter.INSTANCE.toDto(vehicle));
            }
            vehicle.setCustomer(customer);
            applyBrandAndModel(vehicle, request);
            if (request.note() != null) {
                vehicle.setNote(request.note());
            }
            return vehicleRepository.save(vehicle);
        }

        if (StringUtils.isNullOrBlank(request.licensePlate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }

        String normalized = LicensePlateUtil.normalize(request.licensePlate());
        var existingOpt = vehicleRepository.findByNormalizedLicensePlateAndDeleteFlagFalse(normalized);

        if (existingOpt.isPresent()) {
            Vehicle existing = existingOpt.get();
            if (existing.getCustomer() != null) {
                if (existing.getCustomer().getId().equals(customer.getId())) {
                    return existing;
                }
                throw new DuplicateVehicleException(
                        "Biển số xe đã tồn tại và thuộc về khách hàng khác: " + existing.getLicensePlate(),
                        VehicleConverter.INSTANCE.toDto(existing));
            }
            existing.setCustomer(customer);
            applyBrandAndModel(existing, request);
            if (request.note() != null) {
                existing.setNote(request.note());
            }
            return vehicleRepository.save(existing);
        }

        Vehicle vehicle = Vehicle.builder()
                .customer(customer)
                .licensePlate(request.licensePlate().trim())
                .normalizedLicensePlate(normalized)
                .note(request.note())
                .build();
        applyBrandAndModel(vehicle, request);
        return vehicleRepository.save(vehicle);
    }

    private void applyBrandAndModel(Vehicle vehicle, CustomerVehicleRequest request) {
        if (request.brandId() != null) {
            Brand brand = brandRepository
                    .findById(request.brandId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy hãng xe với id: " + request.brandId()));
            vehicle.setBrand(brand);
        }
        if (request.modelId() != null) {
            Model model = modelRepository
                    .findById(request.modelId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy dòng xe với id: " + request.modelId()));
            vehicle.setModel(model);
        }
    }

    private byte[] buildCsv(List<CustomerListItemResponse> items) {
        StringBuilder sb = new StringBuilder();
        sb.append('﻿');
        sb.append(
                "ID,Ten khach hang,So dien thoai,Email,So xe,Bien so xe,Tong luot ghe,Tong chi tieu,Lan ghe cuoi,Nhan,Trang thai,Ngay tao\n");
        for (CustomerListItemResponse it : items) {
            sb.append(csvEscape(it.getId() != null ? it.getId().toString() : ""))
                    .append(',')
                    .append(csvEscape(it.getName()))
                    .append(',')
                    .append(csvEscape(it.getPhone()))
                    .append(',')
                    .append(csvEscape(it.getEmail()))
                    .append(',')
                    .append(it.getVehicleCount())
                    .append(',')
                    .append(csvEscape(it.getVehicles().stream()
                            .map(VehicleRefResponse::licensePlate)
                            .collect(Collectors.joining("; "))))
                    .append(',')
                    .append(it.getTotalVisits())
                    .append(',')
                    .append(it.getTotalSpending() != null ? it.getTotalSpending().toPlainString() : "0")
                    .append(',')
                    .append(it.getLastVisitDate() != null ? it.getLastVisitDate().toString() : "")
                    .append(',')
                    .append(csvEscape(it.getTags().stream()
                            .map(SegmentBadge::getSegmentName)
                            .collect(Collectors.joining("; "))))
                    .append(',')
                    .append(csvEscape(it.getStatus()))
                    .append(',')
                    .append(it.getCreatedAt() != null ? it.getCreatedAt().toString() : "")
                    .append('\n');
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
