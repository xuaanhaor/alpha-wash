package com.alphawash.converter;

import com.alphawash.dto.*;
import com.alphawash.entity.Employee;
import com.alphawash.entity.OrderProductDtl;
import com.alphawash.entity.ServiceItem;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.OrderProductDtlRepository;
import com.alphawash.repository.ServiceItemRepository;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter {

    private final OrderProductDtlRepository orderProductDtlRepository;
    private final ServiceItemRepository serviceItemRepository;

    public List<OrderFullDto> mapToOrderFullDto(List<Object[]> rows, EmployeeRepository employeeRepository) {
        Map<UUID, OrderFullDto> orderMap = new LinkedHashMap<>();
        Set<Long> allEmployeeIds = new HashSet<>();

        // Bước 1: gom tất cả employee ID
        for (Object[] row : rows) {
            String empStr = (String) row[19];
            if (empStr != null && !empStr.isBlank()) {
                for (String idStr : empStr.split(",")) {
                    try {
                        allEmployeeIds.add(Long.parseLong(idStr.trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        // Bước 2: lấy map nhân viên
        Map<Long, Employee> employeeMap = employeeRepository.findAllById(allEmployeeIds).stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity()));

        // Bước 3: batch load service_item cho new-system services (SI_<uuid> codes)
        Set<UUID> serviceItemIds = new HashSet<>();
        for (Object[] row : rows) {
            // osd_catalog_code là cột mới nhất — index 42
            String osdCatalogCode = row.length > 42 ? (String) row[42] : null;
            if (osdCatalogCode != null && osdCatalogCode.startsWith("SI_")) {
                try {
                    serviceItemIds.add(UUID.fromString(osdCatalogCode.substring(3)));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        Map<UUID, ServiceItem> serviceItemMap = serviceItemRepository.findAllById(serviceItemIds)
                .stream().collect(Collectors.toMap(ServiceItem::getId, Function.identity()));

        for (Object[] row : rows) {
            int i = 0;
            UUID orderId = (UUID) row[i++];
            String orderCode = (String) row[i++];
            Timestamp date = (Timestamp) row[i++];
            Time checkIn = (Time) row[i++];
            Time checkOut = (Time) row[i++];
            String paymentStatus = (String) row[i++];
            String paymentType = (String) row[i++];
            BigDecimal tip = (BigDecimal) row[i++];
            BigDecimal vat = (BigDecimal) row[i++];
            BigDecimal discount = (BigDecimal) row[i++];
            BigDecimal totalPrice = (BigDecimal) row[i++];
            String orderNote = (String) row[i++];
            Boolean deleteFlag = (Boolean) row[i++];

            UUID customerId = (UUID) row[i++];
            String customerName = (String) row[i++];
            String customerPhone = (String) row[i++];

            String detailCode = (String) row[i++];
            String status = (String) row[i++];
            String detailNote = (String) row[i++];
            String employeeStr = (String) row[i++];

            UUID vehicleId = (UUID) row[i++];
            String licensePlate = (String) row[i++];
            String imageUrl = (String) row[i++];
            Long brandId = row[i] != null ? ((Number) row[i]).longValue() : null;
            i++;
            String brandName = (String) row[i++];
            String brandCode = (String) row[i++];
            Long modelId = row[i] != null ? ((Number) row[i]).longValue() : null;
            i++;
            String modelName = (String) row[i++];
            String modelCode = (String) row[i++];
            String size = (String) row[i++];

            Long serviceId = row[i] != null ? ((Number) row[i]).longValue() : null;
            i++;
            String serviceCode = (String) row[i++];
            String serviceName = (String) row[i++];
            String serviceTypeCode = (String) row[i++];

            BigDecimal adjustedPrice = (BigDecimal) row[i++];
            Boolean adjustedPriceFlag = (Boolean) row[i++];
            String adjustedPriceReason = (String) row[i++];
            Integer quantity = row[i] != null ? ((Number) row[i]).intValue() : 1; i++;

            Long scId = row[i] != null ? ((Number) row[i]).longValue() : null;
            i++;
            String scCode = (String) row[i++];
            BigDecimal scPrice = (BigDecimal) row[i++];
            String scSize = (String) row[i++];

            // Cột mới: osd.service_catalog_code — index 42
            String osdCatalogCode = row.length > 42 ? (String) row[42] : null;

            // === ORDER ===
            OrderFullDto order = orderMap.computeIfAbsent(orderId, id -> {
                OrderFullDto dto = new OrderFullDto();
                dto.setId(id);
                dto.setCode(orderCode);
                dto.setDate(date);
                dto.setCheckIn(checkIn);
                dto.setCheckOut(checkOut);
                dto.setPaymentStatus(paymentStatus);
                dto.setPaymentType(paymentType);
                dto.setTip(tip);
                dto.setVat(vat);
                dto.setDiscount(discount);
                dto.setTotalPrice(totalPrice);
                dto.setNote(orderNote);
                dto.setDeleteFlag(deleteFlag);
                OrderFullDto.CustomerDTO c = new OrderFullDto.CustomerDTO();
                c.setId(customerId);
                c.setName(customerName);
                c.setPhone(customerPhone);
                dto.setCustomer(c);
                dto.setOrderDetails(new ArrayList<>());
                return dto;
            });

            // === ORDER DETAIL ===
            OrderFullDto.OrderDetailDTO detail = order.getOrderDetails().stream()
                    .filter(d -> d.getCode().equals(detailCode))
                    .findFirst()
                    .orElseGet(() -> {
                        OrderFullDto.OrderDetailDTO d = new OrderFullDto.OrderDetailDTO();
                        d.setCode(detailCode);
                        d.setStatus(status);
                        d.setNote(detailNote);

                        OrderFullDto.VehicleDTO vehicle = new OrderFullDto.VehicleDTO();
                        vehicle.setId(vehicleId);
                        vehicle.setLicensePlate(licensePlate);
                        vehicle.setImageUrl(imageUrl);
                        vehicle.setBrandId(brandId);
                        vehicle.setBrandCode(brandCode);
                        vehicle.setBrandName(brandName);
                        vehicle.setModelId(modelId);
                        vehicle.setModelCode(modelCode);
                        vehicle.setModelName(modelName);
                        vehicle.setSize(size);
                        d.setVehicle(vehicle);

                        d.setEmployees(new ArrayList<>());
                        d.setService(new ArrayList<>());
                        d.setProducts(new ArrayList<>());
                        order.getOrderDetails().add(d);
                        return d;
                    });

            // === EMPLOYEES ===
            if (employeeStr != null && !employeeStr.isBlank()) {
                for (String empIdStr : employeeStr.split(",")) {
                    try {
                        Long empId = Long.parseLong(empIdStr.trim());
                        if (detail.getEmployees().stream()
                                .noneMatch(e -> e.getId().equals(empId))) {
                            Employee emp = employeeMap.get(empId);
                            if (emp != null) {
                                OrderFullDto.EmployeeDTO dto = new OrderFullDto.EmployeeDTO();
                                dto.setId(emp.getId());
                                dto.setName(emp.getName());
                                detail.getEmployees().add(dto);
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // === SERVICE ===
            // Điều kiện: osd tồn tại (osdCatalogCode != null) HOẶC old-system service (serviceId != null)
            if (serviceId != null || osdCatalogCode != null) {
                OrderFullDto.ServiceDTO serviceDto = new OrderFullDto.ServiceDTO();

                if (serviceId != null) {
                    // Old-system service: lấy từ bảng service và service_catalog
                    serviceDto.setId(serviceId);
                    serviceDto.setServiceCode(serviceCode);
                    serviceDto.setServiceName(serviceName);
                    serviceDto.setServiceTypeCode(serviceTypeCode);

                    OrderFullDto.ServiceCatalogDTO sc = new OrderFullDto.ServiceCatalogDTO();
                    sc.setId(scId);
                    sc.setCode(scCode);
                    sc.setListedPrice(scPrice);
                    sc.setSize(scSize);
                    serviceDto.setServiceCatalog(sc);

                } else {
                    // New-system service: catalog code là "SI_<uuid>", lookup từ service_item
                    String itemName = osdCatalogCode; // fallback nếu không tìm thấy
                    UUID itemUuid = null;
                    if (osdCatalogCode.startsWith("SI_")) {
                        try {
                            itemUuid = UUID.fromString(osdCatalogCode.substring(3));
                            ServiceItem si = serviceItemMap.get(itemUuid);
                            if (si != null) {
                                itemName = si.getName();
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }

                    serviceDto.setId(-1L); // không có integer ID trong old system
                    serviceDto.setServiceCode(itemUuid != null ? itemUuid.toString() : osdCatalogCode);
                    serviceDto.setServiceName(itemName);
                    serviceDto.setServiceTypeCode(null);

                    // Catalog DTO với code "SI_<uuid>" — FE dùng để nhận biết new-system service
                    OrderFullDto.ServiceCatalogDTO sc = new OrderFullDto.ServiceCatalogDTO();
                    sc.setId(null);
                    sc.setCode(osdCatalogCode);         // "SI_<uuid>"
                    sc.setListedPrice(adjustedPrice);   // giá niêm yết = giá đã thanh toán
                    sc.setSize(size);                   // size xe (từ model) — hiển thị cột "Loại xe" trên hoá đơn
                    serviceDto.setServiceCatalog(sc);
                }

                serviceDto.setAdjustedPrice(adjustedPrice);
                serviceDto.setAdjustedPriceFlag(adjustedPriceFlag);
                serviceDto.setAdjustedPriceReason(adjustedPriceReason);
                serviceDto.setQuantity(quantity);

                detail.getService().add(serviceDto);
            }
        }

        // === PRODUCTS — load separately to avoid cartesian product ===
        for (OrderFullDto dto : orderMap.values()) {
            for (OrderFullDto.OrderDetailDTO detail : dto.getOrderDetails()) {
                List<OrderProductDtl> productItems =
                        orderProductDtlRepository.findByOrderDetail_CodeAndDeleteFlagFalse(detail.getCode());
                List<OrderFullDto.ProductItemDTO> productDtos = new ArrayList<>();
                for (OrderProductDtl opd : productItems) {
                    OrderFullDto.ProductItemDTO p = new OrderFullDto.ProductItemDTO();
                    p.setId(opd.getId());
                    p.setProductCode(opd.getProduct() != null ? opd.getProduct().getCode() : null);
                    p.setProductName(opd.getProduct() != null ? opd.getProduct().getProductName() : null);
                    p.setUnitPrice(opd.getUnitPrice());
                    p.setQuantity(opd.getQuantity());
                    p.setAdjustedPrice(opd.getAdjustedPrice());
                    p.setAdjustedPriceFlag(opd.getAdjustedPriceFlag());
                    p.setAdjustedPriceReason(opd.getAdjustedPriceReason());
                    p.setDiscount(opd.getDiscount());
                    p.setNote(opd.getNote());
                    p.setCurrentStock(opd.getProduct() != null ? opd.getProduct().getCurrentStock() : null);
                    p.setUnit(opd.getProduct() != null ? opd.getProduct().getUnit() : null);
                    productDtos.add(p);
                }
                detail.setProducts(productDtos);
            }
        }

        return new ArrayList<>(orderMap.values());
    }
}
