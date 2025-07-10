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
}
