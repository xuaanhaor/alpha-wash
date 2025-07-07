package com.alphawash.service.impl;

import com.alphawash.converter.ProductionConverter;
import com.alphawash.entity.Production;
import com.alphawash.repository.ProductionRepository;
import com.alphawash.request.ProductionRequest;
import com.alphawash.service.ProductionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {

    private final ProductionRepository productionRepository;

    @Override
    public Production insertProduction(ProductionRequest request) {
        return productionRepository.save(ProductionConverter.INSTANCE.toEntity(request));
    }

    @Override
    public List<Production> getAllProductions() {
        return productionRepository.findAll();
    }
}
