package com.alphawash.converter;

import com.alphawash.constant.PromoType;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.entity.Employee;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.response.AddPromotionServicesResponse;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public OrderConverter() {}

    public List<OrderFullDto> mapToOrderFullDto(List<Object[]> rows, EmployeeRepository employeeRepository) {
        if (rows == null || rows.isEmpty()) return List.of();

        // orderId -> OrderFullDto
        Map<UUID, OrderFullDto> orderMap = new LinkedHashMap<>();

        // orderId -> (detailCode -> OrderDetailDTO)
        Map<UUID, Map<String, OrderFullDto.OrderDetailDTO>> detailMapByOrder = new HashMap<>();

        // detailCode -> set(serviceCatalogCode) để chống trùng service trong 1 detail
        Map<String, Set<String>> serviceKeySetByDetail = new HashMap<>();

        // orderId -> promoServiceCode -> promoServiceResponse (distinct)
        Map<UUID, Map<String, AddPromotionServicesResponse>> promoServicesByOrder = new HashMap<>();

        // ====== Step 1: gom employee IDs ======
        Set<Long> allEmployeeIds = new HashSet<>();
        for (Object[] row : rows) {
            String employeeStr = extractEmployeeStr(row);
            if (employeeStr != null && !employeeStr.isBlank()) {
                for (String idStr : employeeStr.split(",")) {
                    try {
                        allEmployeeIds.add(Long.parseLong(idStr.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        Map<Long, Employee> employeeMap = allEmployeeIds.isEmpty()
                ? Map.of()
                : employeeRepository.findAllById(allEmployeeIds).stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(), (a, b) -> a));

        // ====== Step 2: map dữ liệu ======
        for (Object[] row : rows) {
            int i = 0;

            // ===== ORDER =====
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

            // ===== CUSTOMER =====
            UUID customerId = (UUID) row[i++];
            String customerName = (String) row[i++];
            String customerPhone = (String) row[i++];

            // ===== ORDER DETAIL =====
            String detailCode = (String) row[i++];
            String detailStatus = (String) row[i++];
            String detailNote = (String) row[i++];
            String employeeStr = (String) row[i++];

            // ===== VEHICLE =====
            UUID vehicleId = (UUID) row[i++];
            String licensePlate = (String) row[i++];
            String imageUrl = (String) row[i++];

            // brand
            Long brandId = toLong(row[i++]);
            String brandName = (String) row[i++];
            String brandCode = (String) row[i++];

            // model
            Long modelId = toLong(row[i++]);
            String modelName = (String) row[i++];
            String modelCode = (String) row[i++];
            String size = (String) row[i++];

            // ===== SERVICE =====
            Long serviceId = toLong(row[i++]);
            String serviceCode = (String) row[i++];
            String serviceName = (String) row[i++];
            String serviceTypeCode = (String) row[i++];

            // ===== ORDER_SERVICE_DTL =====
            BigDecimal adjustedPrice = (BigDecimal) row[i++];
            Boolean adjustedPriceFlag = (Boolean) row[i++];
            String adjustedPriceReason = (String) row[i++];

            // ===== SERVICE CATALOG =====
            Long scId = toLong(row[i++]);
            String scCode = (String) row[i++];
            BigDecimal scPrice = (BigDecimal) row[i++];
            String scSize = (String) row[i++];

            // ===== PROMOTION (nếu có thêm ở cuối query) =====
            // Nếu query hiện tại của bạn CHƯA thêm promotion thì đoạn dưới sẽ bị out of range.
            // Mình check an toàn:
            UUID promoId = null;
            String promoCode = null;
            String promoName = null;
            String promoTypeStr = null;
            BigDecimal promoValue = null;
            Timestamp promoStartTs = null;
            Timestamp promoEndTs = null;

            String promoSvcCode = null;
            String promoSvcName = null;
            BigDecimal promoSvcDiscountAmount = null;
            BigDecimal promoSvcDiscountPercent = null;

            if (i < row.length) {
                promoId = (UUID) safeGet(row, i++);
                promoCode = (String) safeGet(row, i++);
                promoName = (String) safeGet(row, i++);
                promoTypeStr = (String) safeGet(row, i++);
                promoValue = (BigDecimal) safeGet(row, i++);
                promoStartTs = (Timestamp) safeGet(row, i++);
                promoEndTs = (Timestamp) safeGet(row, i++);

                promoSvcCode = (String) safeGet(row, i++);
                promoSvcName = (String) safeGet(row, i++);
                promoSvcDiscountAmount = (BigDecimal) safeGet(row, i++);
                promoSvcDiscountPercent = (BigDecimal) safeGet(row, i++);
            }

            // ===== build/get ORDER =====
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

                if (customerId != null) {
                    dto.setCustomer(new OrderFullDto.CustomerDTO(customerId, customerName, customerPhone));
                }

                dto.setOrderDetails(new ArrayList<>());
                // promotion sẽ set sau (nếu có)
                return dto;
            });

            Map<String, OrderFullDto.OrderDetailDTO> detailMap =
                    detailMapByOrder.computeIfAbsent(orderId, k -> new LinkedHashMap<>());

            OrderFullDto.OrderDetailDTO detail = detailMap.get(detailCode);
            if (detail == null) {
                detail = new OrderFullDto.OrderDetailDTO();
                detail.setCode(detailCode);
                detail.setStatus(detailStatus);
                detail.setNote(detailNote);

                OrderFullDto.VehicleDTO vehicle = new OrderFullDto.VehicleDTO();
                vehicle.setId(vehicleId);
                vehicle.setLicensePlate(licensePlate);
                vehicle.setImageUrl(imageUrl);
                vehicle.setBrandId(brandId);
                vehicle.setBrandName(brandName);
                vehicle.setBrandCode(brandCode);
                vehicle.setModelId(modelId);
                vehicle.setModelName(modelName);
                vehicle.setModelCode(modelCode);
                vehicle.setSize(size);
                detail.setVehicle(vehicle);

                detail.setEmployees(new ArrayList<>());
                detail.setService(new ArrayList<>());

                detailMap.put(detailCode, detail);
                order.getOrderDetails().add(detail);

                serviceKeySetByDetail.put(detailCode, new HashSet<>());
            }

            // ===== EMPLOYEES =====
            if (employeeStr != null && !employeeStr.isBlank()) {
                Set<Long> existingEmpIds = detail.getEmployees().stream()
                        .map(OrderFullDto.EmployeeDTO::getId)
                        .collect(Collectors.toSet());

                for (String empIdStr : employeeStr.split(",")) {
                    try {
                        Long empId = Long.parseLong(empIdStr.trim());
                        if (existingEmpIds.contains(empId)) continue;

                        Employee emp = employeeMap.get(empId);
                        if (emp != null) {
                            OrderFullDto.EmployeeDTO e = new OrderFullDto.EmployeeDTO(emp.getId(), emp.getName());
                            detail.getEmployees().add(e);
                            existingEmpIds.add(empId);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            // ===== SERVICES (chống trùng theo scCode) =====
            if (scCode != null) {
                Set<String> serviceKeys = serviceKeySetByDetail.get(detailCode);
                if (serviceKeys.add(scCode)) {
                    OrderFullDto.ServiceDTO svc = new OrderFullDto.ServiceDTO();
                    svc.setId(serviceId);
                    svc.setServiceCode(serviceCode);
                    svc.setServiceName(serviceName);
                    svc.setServiceTypeCode(serviceTypeCode);
                    svc.setAdjustedPrice(adjustedPrice);
                    svc.setAdjustedPriceFlag(adjustedPriceFlag);
                    svc.setAdjustedPriceReason(adjustedPriceReason);

                    OrderFullDto.ServiceCatalogDTO sc = new OrderFullDto.ServiceCatalogDTO();
                    sc.setId(scId);
                    sc.setCode(scCode);
                    sc.setListedPrice(scPrice);
                    sc.setSize(scSize);
                    svc.setServiceCatalog(sc);

                    detail.getService().add(svc);
                }
            }

            // ===== PROMOTION (set 1 lần / order) + gom promotion_service =====
            if (promoId != null) {
                if (order.getPromotion() == null) {
                    OrderFullDto.PromotionDTO promo = new OrderFullDto.PromotionDTO();
                    promo.setPromoId(promoId);
                    promo.setPromoCode(promoCode);
                    promo.setPromoName(promoName);
                    promo.setValue(promoValue);
                    promo.setStartDate(toLocalDateTime(promoStartTs));
                    promo.setEndDate(toLocalDateTime(promoEndTs));

                    if (promoTypeStr != null) {
                        try {
                            promo.setPromoType(PromoType.valueOf(promoTypeStr));
                        } catch (Exception ignored) {
                            // nếu DB lưu khác enum name, bạn tự map tại đây
                        }
                    }

                    promo.setServices(new ArrayList<>());
                    order.setPromotion(promo);
                }

                // gom promo services distinct theo serviceCode
                if (promoSvcCode != null && !promoSvcCode.isBlank()) {
                    Map<String, AddPromotionServicesResponse> promoSvcMap =
                            promoServicesByOrder.computeIfAbsent(orderId, k -> new LinkedHashMap<>());

                    promoSvcMap.putIfAbsent(promoSvcCode,
                            AddPromotionServicesResponse.builder()
                                    .serviceCode(promoSvcCode)
                                    .serviceName(promoSvcName)
                                    .discountAmount(promoSvcDiscountAmount)
                                    .discountPercent(promoSvcDiscountPercent)
                                    .build()
                    );
                }
            }
        }

        // ===== final: gắn promotion.services vào promotion =====
        for (Map.Entry<UUID, OrderFullDto> e : orderMap.entrySet()) {
            UUID orderId = e.getKey();
            OrderFullDto order = e.getValue();

            if (order.getPromotion() != null) {
                Map<String, AddPromotionServicesResponse> promoSvcMap = promoServicesByOrder.get(orderId);
                if (promoSvcMap != null) {
                    order.getPromotion().setServices(new ArrayList<>(promoSvcMap.values()));
                }
            }
        }

        return new ArrayList<>(orderMap.values());
    }

    // ========== helpers ==========

    private static Object safeGet(Object[] row, int idx) {
        return (idx >= 0 && idx < row.length) ? row[idx] : null;
    }

    private static Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    /**
     * Lấy employeeStr từ row để gom ID trước khi map.
     * Dựa theo query hiện tại của bạn: employeeStr nằm sau (order_detail_note)
     * => index 19 nếu đúng thứ tự bạn đang parse (tương ứng row[i++] ở phần OrderDetail).
     */
    private static String extractEmployeeStr(Object[] row) {
        // Với parse của bạn: employeeStr nằm vị trí:
        // 0..12 order, 13..15 customer, 16 detailCode, 17 status, 18 detailNote, 19 employeeStr
        if (row == null || row.length <= 19) return null;
        Object v = row[19];
        return v == null ? null : v.toString();
    }
}
