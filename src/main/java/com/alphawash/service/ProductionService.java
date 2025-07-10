package com.alphawash.service;

import com.alphawash.entity.Production;
import com.alphawash.request.ProductionRequest;
import com.alphawash.response.ProductionResponse;

import java.util.List;

public interface ProductionService {
    ProductionResponse insertProduction(ProductionRequest request);

    List<ProductionResponse> getAllProductions();

    ProductionResponse getProductionById(Long id);
}
