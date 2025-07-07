package com.alphawash.service;

import com.alphawash.entity.Production;
import com.alphawash.request.ProductionRequest;
import java.util.List;

public interface ProductionService {
    Production insertProduction(ProductionRequest request);

    List<Production> getAllProductions();
}
