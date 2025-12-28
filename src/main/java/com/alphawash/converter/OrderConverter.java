package com.alphawash.converter;

import com.alphawash.constant.PromoType;
import com.alphawash.dto.OrderFullDto;
import com.alphawash.entity.Employee;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.response.AddPromotionServicesResponse;
import java.lang.reflect.Constructor;
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

    /**
     * - SERVICE order: services lấy từ order_service_dtl -> service_catalog -> service
     * - COMBO order: chỉ trả 1 item trong "service" đại diện combo (không có id/serviceCode/serviceName/serviceTypeCode),
     *   và comboCatalog.services lấy từ service_combo_quality (quantity).
     */
    public List<OrderFullDto> mapToOrderFullDto(List<Object[]> rows, EmployeeRepository employeeRepository) {
        if (rows == null || rows.isEmpty()) return List.of();

        Map<Long, Employee> employeeMap = preloadEmployees(rows, employeeRepository);

        Map<UUID, OrderFullDto> orderMap = new LinkedHashMap<>();
        Map<UUID, Map<String, OrderFullDto.OrderDetailDTO>> detailMapByOrder = new HashMap<>();

        // dedupe service rows for SERVICE orders (detailCode -> keys)
        Map<String, Set<String>> serviceKeyByDetail = new HashMap<>();

        // combo catalog cache per detail+catalog
        Map<String, OrderFullDto.ComboCatalogDTO> comboCatalogCache = new HashMap<>();
        Map<String, Set<String>> comboLineKeyByDetailCombo = new HashMap<>();

        // ensure only 1 "service item" for each combo in a detail
        Set<String> comboServiceItemAdded = new HashSet<>();

        // promotion services dedupe per order
        Map<UUID, Set<String>> promoSvcKeyByOrder = new HashMap<>();

        for (Object[] row : rows) {
            int i = 0;

            // ===== ORDER (14) =====
            UUID orderId = (UUID) row[i++];
            String orderCode = (String) row[i++];
            Timestamp orderDate = (Timestamp) row[i++];
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
            Timestamp createdAt = (Timestamp) row[i++];

            // ===== CUSTOMER (3) =====
            UUID customerId = (UUID) row[i++];
            String customerName = (String) row[i++];
            String customerPhone = (String) row[i++];

            // ===== ORDER DETAIL (5) =====
            String detailCode = (String) row[i++];
            String orderType = (String) row[i++]; // SERVICE / COMBO
            String detailStatus = (String) row[i++];
            String detailNote = (String) row[i++];
            String employeeStr = (String) row[i++];

            // ===== VEHICLE (3) =====
            UUID vehicleId = (UUID) row[i++];
            String licensePlate = (String) row[i++];
            String vehicleImageUrl = (String) row[i++];

            // ===== BRAND (3) =====
            Long brandId = toLong(row[i++]);
            String brandCode = (String) row[i++];
            String brandName = (String) row[i++];

            // ===== MODEL (4) =====
            Long modelId = toLong(row[i++]);
            String modelCode = (String) row[i++];
            String modelName = (String) row[i++];
            String modelSize = (String) row[i++];

            // ===== SERVICE (4) - only for SERVICE orders =====
            Long serviceId = toLong(row[i++]);
            String serviceCode = (String) row[i++];
            String serviceName = (String) row[i++];
            String serviceTypeCode = (String) row[i++];

            // ===== OSD (4) =====
            BigDecimal adjustedPrice = (BigDecimal) row[i++];
            Boolean adjustedPriceFlag = (Boolean) row[i++];
            String adjustedPriceReason = (String) row[i++];
            String serviceComboCatalogCode = (String) row[i++];

            // ===== SERVICE CATALOG (4) - only for SERVICE orders =====
            Long scId = toLong(row[i++]);
            String scCode = (String) row[i++];
            BigDecimal scPrice = (BigDecimal) row[i++];
            String scSize = (String) row[i++];

            // ===== COMBO CATALOG (8) - for COMBO orders =====
            Long comboCatalogId = toLong(row[i++]);
            String comboCatalogCode = (String) row[i++];
            String comboName = (String) row[i++];
            String comboSize = (String) row[i++];
            BigDecimal comboCatalogPrice = (BigDecimal) row[i++];
            Boolean comboPriceIncludeTax = (Boolean) row[i++];
            String comboServiceCatalogCode = (String) row[i++];
            String comboServiceName = (String) row[i++];
            Integer comboServiceQuantity = toInteger(row[i++]);

            // ===== PROMOTION (11) =====
            UUID promoId = (UUID) row[i++];
            String promoCode = (String) row[i++];
            String promoName = (String) row[i++];
            String promoTypeStr = (String) row[i++];
            BigDecimal promoValue = (BigDecimal) row[i++];
            Timestamp promoStart = (Timestamp) row[i++];
            Timestamp promoEnd = (Timestamp) row[i++];
            String promoServiceCode = (String) row[i++];
            String promoServiceName = (String) row[i++];
            BigDecimal promoDiscountAmount = (BigDecimal) row[i++];
            BigDecimal promoDiscountPercent = (BigDecimal) row[i++];

            // ===== build/get ORDER =====
            OrderFullDto order = orderMap.computeIfAbsent(orderId, id -> {
                OrderFullDto dto = new OrderFullDto();
                dto.setId(id);
                dto.setCode(orderCode);
                dto.setDate(orderDate);
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
                dto.setOrderDetails(new ArrayList<>());

                if (customerId != null) {
                    dto.setCustomer(new OrderFullDto.CustomerDTO(customerId, customerName, customerPhone));
                }
                return dto;
            });

            // ===== build/get DETAIL =====
            Map<String, OrderFullDto.OrderDetailDTO> detailMap =
                    detailMapByOrder.computeIfAbsent(orderId, k -> new LinkedHashMap<>());

            OrderFullDto.OrderDetailDTO detail = detailMap.get(detailCode);
            if (detail == null) {
                detail = new OrderFullDto.OrderDetailDTO();
                detail.setCode(detailCode);
                detail.setOrderType(orderType);
                detail.setStatus(detailStatus);
                detail.setNote(detailNote);

                OrderFullDto.VehicleDTO v = new OrderFullDto.VehicleDTO();
                v.setId(vehicleId);
                v.setLicensePlate(licensePlate);
                v.setImageUrl(vehicleImageUrl);
                v.setBrandId(brandId);
                v.setBrandCode(brandCode);
                v.setBrandName(brandName);
                v.setModelId(modelId);
                v.setModelCode(modelCode);
                v.setModelName(modelName);
                v.setSize(modelSize);

                detail.setVehicle(v);
                detail.setEmployees(new ArrayList<>());
                detail.setService(new ArrayList<>());

                detailMap.put(detailCode, detail);
                order.getOrderDetails().add(detail);

                serviceKeyByDetail.put(detailCode, new HashSet<>());
            }

            // ===== EMPLOYEES =====
            attachEmployees(detail, employeeStr, employeeMap);

            // ===== PROMOTION: order-level =====
            if (promoId != null) {
                if (order.getPromotion() == null) {
                    OrderFullDto.PromotionDTO promo = new OrderFullDto.PromotionDTO();
                    promo.setPromoId(promoId);
                    promo.setPromoCode(promoCode);
                    promo.setPromoName(promoName);
                    promo.setValue(promoValue);
                    promo.setStartDate(toLdt(promoStart));
                    promo.setEndDate(toLdt(promoEnd));
                    promo.setServices(new ArrayList<>());
                    if (promoTypeStr != null) {
                        try { promo.setPromoType(PromoType.valueOf(promoTypeStr)); } catch (Exception ignored) {}
                    }
                    order.setPromotion(promo);
                    promoSvcKeyByOrder.put(orderId, new HashSet<>());
                }

                if (promoServiceCode != null && order.getPromotion() != null) {
                    Set<String> kset = promoSvcKeyByOrder.get(orderId);
                    if (kset.add(promoServiceCode)) {
                        AddPromotionServicesResponse line = newPromoServiceLine(
                                promoServiceCode, promoServiceName, promoDiscountAmount, promoDiscountPercent
                        );
                        if (line != null) order.getPromotion().getServices().add(line);
                    }
                }
            }

            boolean isCombo = "COMBO".equalsIgnoreCase(orderType);

            // ===== COMBO: create 1 service item + build combo lines =====
            if (isCombo && comboCatalogCode != null) {
                String comboKey = detailCode + "|" + comboCatalogCode;

                // comboCatalog object
                OrderFullDto.ComboCatalogDTO comboCatalogDto = comboCatalogCache.computeIfAbsent(comboKey, k -> {
                    OrderFullDto.ComboCatalogDTO cc = order.new ComboCatalogDTO();
                    cc.setCatalogCode(comboCatalogCode);
                    cc.setComboName(comboName);
                    cc.setSize(comboSize);
                    cc.setPrice(comboCatalogPrice);
                    cc.setPriceIncludeTax(comboPriceIncludeTax);
                    cc.setServices(new ArrayList<>());
                    comboLineKeyByDetailCombo.put(comboKey, new HashSet<>());
                    return cc;
                });

                // build comboCatalog.services from scq
                if (comboServiceCatalogCode != null && comboServiceQuantity != null) {
                    Set<String> keys = comboLineKeyByDetailCombo.get(comboKey);
                    if (keys.add(comboServiceCatalogCode)) {
                        OrderFullDto.ComboServiceDTO line = order.new ComboServiceDTO();
                        line.setServiceCatalogCode(comboServiceCatalogCode);
                        line.setServiceName(comboServiceName);
                        line.setQuantity(comboServiceQuantity);
                        comboCatalogDto.getServices().add(line);
                    }
                }

                // add 1 "service item" đại diện combo (không có id/serviceCode/serviceName/serviceTypeCode)
                if (comboServiceItemAdded.add(comboKey)) {
                    OrderFullDto.ServiceDTO svc = new OrderFullDto.ServiceDTO();
                    svc.setAdjustedPrice(adjustedPrice);
                    svc.setAdjustedPriceFlag(adjustedPriceFlag);
                    svc.setAdjustedPriceReason(adjustedPriceReason);
                    svc.setServiceCatalog(null);
                    svc.setServiceComboCatalog(comboCatalogDto);
                    detail.getService().add(svc);
                }

                continue;
            }

            // ===== SERVICE: add service normally =====
            if (!isCombo && scCode != null) {
                String svcKey = scCode + "|" + (serviceComboCatalogCode == null ? "" : serviceComboCatalogCode);
                Set<String> keys = serviceKeyByDetail.get(detailCode);

                if (keys.add(svcKey)) {
                    OrderFullDto.ServiceDTO svc = new OrderFullDto.ServiceDTO();
                    svc.setId(serviceId);
                    svc.setServiceCode(serviceCode);
                    svc.setServiceName(serviceName);
                    svc.setServiceTypeCode(serviceTypeCode);
                    svc.setAdjustedPrice(adjustedPrice);
                    svc.setAdjustedPriceFlag(adjustedPriceFlag);
                    svc.setAdjustedPriceReason(adjustedPriceReason);

                    OrderFullDto.ServiceCatalogDTO scDto = new OrderFullDto.ServiceCatalogDTO();
                    scDto.setId(scId);
                    scDto.setCode(scCode);
                    scDto.setListedPrice(scPrice);
                    scDto.setSize(scSize);
                    svc.setServiceCatalog(scDto);

                    detail.getService().add(svc);
                }
            }
        }

        return new ArrayList<>(orderMap.values());
    }

    private Map<Long, Employee> preloadEmployees(List<Object[]> rows, EmployeeRepository employeeRepository) {
        // employee_id index in SELECT: order(14) + customer(3) + detail(5) => employee_id = 21 (0-based)
        int employeeIdx = 21;

        Set<Long> ids = new HashSet<>();
        for (Object[] row : rows) {
            if (row == null || row.length <= employeeIdx || row[employeeIdx] == null) continue;
            String s = row[employeeIdx].toString();
            if (s.isBlank()) continue;
            for (String part : s.split(",")) {
                try { ids.add(Long.parseLong(part.trim())); } catch (Exception ignored) {}
            }
        }
        if (ids.isEmpty()) return Map.of();

        return employeeRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(), (a, b) -> a));
    }

    private void attachEmployees(OrderFullDto.OrderDetailDTO detail, String employeeStr, Map<Long, Employee> employeeMap) {
        if (employeeStr == null || employeeStr.isBlank() || employeeMap.isEmpty()) return;

        Set<Long> existing = detail.getEmployees().stream()
                .map(OrderFullDto.EmployeeDTO::getId)
                .collect(Collectors.toSet());

        for (String empIdStr : employeeStr.split(",")) {
            try {
                Long empId = Long.parseLong(empIdStr.trim());
                if (existing.contains(empId)) continue;
                Employee emp = employeeMap.get(empId);
                if (emp != null) {
                    detail.getEmployees().add(new OrderFullDto.EmployeeDTO(emp.getId(), emp.getName()));
                    existing.add(empId);
                }
            } catch (Exception ignored) {}
        }
    }

    private static Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return null; }
    }

    private static Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return null; }
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    private static AddPromotionServicesResponse newPromoServiceLine(
            String serviceCode, String serviceName, BigDecimal discountAmount, BigDecimal discountPercent
    ) {
        try {
            AddPromotionServicesResponse obj = AddPromotionServicesResponse.class.getDeclaredConstructor().newInstance();
            setIfExists(obj, "setServiceCode", String.class, serviceCode);
            setIfExists(obj, "setServiceName", String.class, serviceName);
            setIfExists(obj, "setDiscountAmount", BigDecimal.class, discountAmount);
            setIfExists(obj, "setDiscountPercent", BigDecimal.class, discountPercent);
            return obj;
        } catch (Exception e) {
            try {
                Constructor<AddPromotionServicesResponse> c =
                        AddPromotionServicesResponse.class.getDeclaredConstructor(
                                String.class, String.class, BigDecimal.class, BigDecimal.class
                        );
                return c.newInstance(serviceCode, serviceName, discountAmount, discountPercent);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private static void setIfExists(Object target, String setter, Class<?> type, Object value) {
        try { target.getClass().getMethod(setter, type).invoke(target, value); } catch (Exception ignored) {}
    }
}
