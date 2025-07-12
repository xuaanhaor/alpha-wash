package com.alphawash.service.impl;

import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.VehicleDto;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.service.VehicleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;

    @Override
    public List<VehicleDto> search() {
        var result = repository.findAll();
        return VehicleConverter.INSTANCE.toDto(result);
    }
}
