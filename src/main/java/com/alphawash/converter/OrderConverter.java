package com.alphawash.converter;

import com.alphawash.dto.*;
import com.alphawash.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class OrderConverter {

    private final EmployeeRepository employeeRepository;

    public OrderConverter(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public OrderTableDto mapFromSingleRow(Object[] row) {
        int i = 0;

        OrderTableDto order = new OrderTableDto();
        order.setId((UUID) row[i++]);
        order.setOrderDate((Timestamp) row[i++]);
        order.setCheckIn((Time) row[i++]);
        order.setCheckOut((Time) row[i++]);
        order.setNote((String) row[i++]);
        order.setPaymentStatus((String) row[i++]);
        order.setPaymentType((String) row[i++]);
        order.setTip((BigDecimal) row[i++]);
        order.setVat((BigDecimal) row[i++]);
        order.setDiscount((BigDecimal) row[i++]);
        order.setTotalPrice((BigDecimal) row[i++]);

        // Customer
        OrderTableDto.CustomerDTO customer = new OrderTableDto.CustomerDTO();
        customer.setId((UUID) row[i++]);
        customer.setCustomerName((String) row[i++]);
        customer.setPhone((String) row[i++]);
        order.setCustomer(customer);

        // Employees
        List<OrderTableDto.EmployeeDTO> employees = new ArrayList<>();
        String employeeStr = (String) row[i++];
        if (employeeStr != null && !employeeStr.isBlank()) {
            for (String empIdStr : employeeStr.split("-")) {
                try {
                    Long empId = Long.parseLong(empIdStr.trim());
                    employeeRepository.findById(empId).ifPresent(emp -> {
                        OrderTableDto.EmployeeDTO dto = new OrderTableDto.EmployeeDTO();
                        dto.setId(emp.getId());
                        dto.setEmployeeName(emp.getName());
                        employees.add(dto);
                    });
                } catch (NumberFormatException ignored) {}
            }
        }

        String status = (String) row[i++];
        String detailNote = (String) row[i++];

        // Vehicle
        OrderTableDto.VehicleDTO vehicle = new OrderTableDto.VehicleDTO();
        vehicle.setId((UUID) row[i++]);
        vehicle.setLicensePlate((String) row[i++]);
        vehicle.setImageUrl((String) row[i++]);
        vehicle.setBrandId(toLong(row[i++]));
        vehicle.setBrandCode((String) row[i++]);
        vehicle.setBrandName((String) row[i++]);
        vehicle.setModelId(toLong(row[i++]));
        vehicle.setModelCode((String) row[i++]);
        vehicle.setModelName((String) row[i++]);
        vehicle.setSize((String) row[i++]);

        // Service
        OrderTableDto.ServiceDTO service = new OrderTableDto.ServiceDTO();
        service.setId(toLong(row[i++]));
        service.setServiceCode((String) row[i++]);
        service.setServiceName((String) row[i++]);
        service.setServiceTypeCode((String) row[i++]);

        // Catalog
        OrderTableDto.ServiceCatalogDTO catalog = new OrderTableDto.ServiceCatalogDTO();
        catalog.setId(toLong(row[i++]));
        catalog.setServiceCatalogCode((String) row[i++]);
        catalog.setPrice((BigDecimal) row[i++]);
        catalog.setSize((String) row[i++]);

        service.setServiceCatalog(catalog);

        // Detail
        OrderTableDto.OrderDetailDTO detail = new OrderTableDto.OrderDetailDTO();
        detail.setEmployees(employees);
        detail.setStatus(status);
        detail.setNote(detailNote);
        detail.setVehicle(vehicle);
        detail.setService(service);

        // Set vào order
        order.setOrderDetails(Collections.singletonList(detail));

        return order;
    }

    public List<OrderTableDto> mapFromRawObjectList(List<Object[]> rows) {
        Map<UUID, OrderTableDto> orderMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            int i = 0;

            UUID orderId = (UUID) row[i++];
            OrderTableDto order = orderMap.get(orderId);

            if (order == null) {
                order = new OrderTableDto();
                order.setId(orderId);
                order.setOrderDate((Timestamp) row[i++]);
                order.setCheckIn((Time) row[i++]);
                order.setCheckOut((Time) row[i++]);
                order.setNote((String) row[i++]);
                order.setPaymentStatus((String) row[i++]);
                order.setPaymentType((String) row[i++]);
                order.setTip((BigDecimal) row[i++]);
                order.setVat((BigDecimal) row[i++]);
                order.setDiscount((BigDecimal) row[i++]);
                order.setTotalPrice((BigDecimal) row[i++]);

                OrderTableDto.CustomerDTO customer = new OrderTableDto.CustomerDTO();
                customer.setId((UUID) row[i++]);
                customer.setCustomerName((String) row[i++]);
                customer.setPhone((String) row[i++]);
                order.setCustomer(customer);

                order.setOrderDetails(new ArrayList<>());
                orderMap.put(orderId, order);
            } else {
                i += 8; // đã đọc 8 trường customer
            }

            // employee_ids
            String employeeStr = (String) row[i++];
            List<OrderTableDto.EmployeeDTO> employees = new ArrayList<>();
            if (employeeStr != null && !employeeStr.isBlank()) {
                for (String empIdStr : employeeStr.split("-")) {
                    try {
                        Long empId = Long.parseLong(empIdStr.trim());
                        employeeRepository.findById(empId).ifPresent(emp -> {
                            OrderTableDto.EmployeeDTO dto = new OrderTableDto.EmployeeDTO();
                            dto.setId(emp.getId());
                            dto.setEmployeeName(emp.getName());
                            employees.add(dto);
                        });
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            // tiến độ thi công
            String status = (String) row[i++];
            // note đơn chi tiết
            String note = (String) row[i++];
            // vehicle
            OrderTableDto.VehicleDTO vehicle = new OrderTableDto.VehicleDTO();
            vehicle.setId((UUID) row[i++]);
            vehicle.setLicensePlate((String) row[i++]);
            vehicle.setImageUrl((String) row[i++]);
            vehicle.setBrandId(toLong(row[i++])); // fix kiểu
            vehicle.setBrandCode((String) row[i++]);
            vehicle.setBrandName((String) row[i++]);
            vehicle.setModelId(toLong(row[i++])); // fix kiểu
            vehicle.setModelCode((String) row[i++]);
            vehicle.setModelName((String) row[i++]);
            vehicle.setSize((String) row[i++]);

            // === service ===
            OrderTableDto.ServiceDTO service = new OrderTableDto.ServiceDTO();
            service.setId(toLong(row[i++])); // index 20
            service.setServiceCode((String) row[i++]); // index 21
            service.setServiceName((String) row[i++]); // index 21
            service.setServiceTypeCode((String) row[i++]); // index 22

            // === service_catalog ===
            OrderTableDto.ServiceCatalogDTO catalog = new OrderTableDto.ServiceCatalogDTO();
            catalog.setId(toLong(row[i++])); // index 22
            catalog.setServiceCatalogCode((String) row[i++]); // index 23
            catalog.setPrice((BigDecimal) row[i++]); // index 23
            catalog.setSize((String) row[i++]); // index 24

            // link catalog vào service
            service.setServiceCatalog(catalog);

            // detail
            OrderTableDto.OrderDetailDTO detail = new OrderTableDto.OrderDetailDTO();
            detail.setStatus(status);
            detail.setNote(note);
            detail.setEmployees(employees);
            detail.setVehicle(vehicle);
            detail.setService(service);

            order.getOrderDetails().add(detail);
        }

        return new ArrayList<>(orderMap.values());
    }

    private Long toLong(Object obj) {
        if (obj instanceof Number n) {
            return n.longValue();
        }
        return null;
    }
}
