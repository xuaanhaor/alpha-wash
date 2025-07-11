package com.alphawash.service.impl;

import com.alphawash.converter.ProductionConverter;
import com.alphawash.entity.Production;
import com.alphawash.repository.ProductionRepository;
import com.alphawash.request.ProductionRequest;
import com.alphawash.response.ProductionResponse;
import com.alphawash.service.ProductionService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {

    private final ProductionRepository productionRepository;
    private final ProductionConverter productionConverter;

    @Override
    public ProductionResponse insertProduction(ProductionRequest request) {
        Production entity = productionConverter.toEntity(request);
        entity = productionRepository.save(entity);
        // Convert the saved entity to a response object
        return productionConverter.toResponse(entity);
    }

    @Override
    public List<ProductionResponse> getAllProductions() {
        return productionRepository.findAll().stream()
                .map(productionConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductionResponse getProductionById(Long id) {
        Production production = productionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi với id: " + id));
        return productionConverter.toResponse(production);
    }

    @Override
    public Production updateProduction(Long id, ProductionRequest request) {
        Production existing = productionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu rửa xe với id: " + id));

        // Cập nhật thông tin từ request vào entity hiện có
        existing.setStt(request.stt());
        existing.setDate(request.date());
        existing.setTimeIn(request.timeIn());
        existing.setTimeOut(request.timeOut());
        existing.setPlateNumber(request.plateNumber());
        existing.setCustomerName(request.customerName());
        existing.setSdt(request.sdt());
        existing.setCarCompany(request.carCompany());
        existing.setVehicleLine(request.vehicleLine());
        existing.setService(request.service());
        existing.setCarSize(request.carSize());
        existing.setStatus(request.status());
        existing.setStatusPayment(request.statusPayment());
        existing.setEmployees(String.join(",", request.employees()));

        return productionRepository.save(existing);
    }

}
