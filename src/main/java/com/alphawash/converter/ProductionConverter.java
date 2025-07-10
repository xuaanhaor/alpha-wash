package com.alphawash.converter;

import com.alphawash.entity.Production;
import com.alphawash.request.ProductionRequest;
import com.alphawash.response.ProductionResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductionConverter {

    public Production toEntity(ProductionRequest request) {
        return Production.builder()
                .stt(request.stt())
                .date(request.date())
                .timeIn(request.timeIn())
                .timeOut(request.timeOut())
                .plateNumber(request.plateNumber())
                .customerName(request.customerName())
                .sdt(request.sdt())
                .carCompany(request.carCompany())
                .vehicleLine(request.vehicleLine())
                .service(request.service())
                .carSize(request.carSize())
                .status(request.status())
                .employees(String.join(",", request.employees()))
                .build();
    }

    public ProductionResponse toResponse(Production entity) {
        List<String> employees = entity.getEmployees() == null || entity.getEmployees().isEmpty()
                ? List.of()
                : Arrays.asList(entity.getEmployees().split(","));

        return new ProductionResponse(
                entity.getId(),
                entity.getStt(),
                entity.getDate(),
                entity.getTimeIn(),
                entity.getTimeOut(),
                entity.getPlateNumber(),
                entity.getCustomerName(),
                entity.getSdt(),
                entity.getCarCompany(),
                entity.getVehicleLine(),
                entity.getService(),
                entity.getCarSize(),
                entity.getStatus(),
                employees
        );
    }
}
