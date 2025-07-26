package com.alphawash.converter;

import com.alphawash.dto.*;
import com.alphawash.entity.Employee;
import com.alphawash.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public OrderConverter() {}

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
            Long brandId = ((Number) row[i++]).longValue();
            String brandName = (String) row[i++];
            String brandCode = (String) row[i++];
            Long modelId = ((Number) row[i++]).longValue();
            String modelName = (String) row[i++];
            String modelCode = (String) row[i++];
            String size = (String) row[i++];

            Long serviceId = ((Number) row[i++]).longValue();
            String serviceCode = (String) row[i++];
            String serviceName = (String) row[i++];
            String serviceTypeCode = (String) row[i++];

            Long scId = ((Number) row[i++]).longValue();
            String scCode = (String) row[i++];
            BigDecimal scPrice = (BigDecimal) row[i++];
            String scSize = (String) row[i++];

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
            OrderFullDto.ServiceDTO service = new OrderFullDto.ServiceDTO();
            service.setId(serviceId);
            service.setServiceCode(serviceCode);
            service.setServiceName(serviceName);
            service.setServiceTypeCode(serviceTypeCode);
            OrderFullDto.ServiceCatalogDTO sc = new OrderFullDto.ServiceCatalogDTO();
            sc.setId(scId);
            sc.setServiceCatalogCode(scCode);
            sc.setPrice(scPrice);
            sc.setSize(scSize);
            service.setServiceCatalog(sc);
            detail.getService().add(service);
        }

        return new ArrayList<>(orderMap.values());
    }
}
