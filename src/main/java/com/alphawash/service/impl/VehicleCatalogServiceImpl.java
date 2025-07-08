package com.alphawash.service.impl;

import com.alphawash.repository.VehicleCatalogRepository;
import com.alphawash.service.VehicleCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleCatalogServiceImpl implements VehicleCatalogService {

    private final VehicleCatalogRepository vehicleCatalogRepository;

    @Override
    public List<String> getAllBrands() {
        return vehicleCatalogRepository.findAllDistinctBrands();
    }
}
