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

            // vehicle
            OrderTableDto.VehicleDTO vehicle = new OrderTableDto.VehicleDTO();
            vehicle.setId((UUID) row[i++]);
            vehicle.setLicensePlate((String) row[i++]);
            vehicle.setImageUrl((String) row[i++]);
            vehicle.setBrandId(toLong(row[i++])); // fix kiểu
            vehicle.setBrandName((String) row[i++]);
            vehicle.setModelId(toLong(row[i++])); // fix kiểu
            vehicle.setModelName((String) row[i++]);
            vehicle.setSize((String) row[i++]);

            // === service ===
            OrderTableDto.ServiceDTO service = new OrderTableDto.ServiceDTO();
            service.setId(toLong(row[i++])); // index 20
            service.setServiceName((String) row[i++]); // index 21

            // === service_catalog ===
            OrderTableDto.ServiceCatalogDTO catalog = new OrderTableDto.ServiceCatalogDTO();
            catalog.setId(toLong(row[i++])); // index 22
            catalog.setPrice((BigDecimal) row[i++]); // index 23
            catalog.setSize((String) row[i++]); // index 24

            // link catalog vào service
            service.setServiceCatalog(catalog);

            // detail
            OrderTableDto.OrderDetailDTO detail = new OrderTableDto.OrderDetailDTO();
            detail.setEmployee(employees);
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
