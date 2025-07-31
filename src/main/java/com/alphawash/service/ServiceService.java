package com.alphawash.service;

import com.alphawash.dto.ServiceDto;
import com.alphawash.request.CreateBasicServiceRequest;
import com.alphawash.response.BasicServiceResponse;
import java.util.List;

public interface ServiceService {
    List<BasicServiceResponse> getAllBasicServices();

    List<ServiceDto> getAll();

    ServiceDto getById(Long id);

    ServiceDto create(ServiceDto dto);

    ServiceDto update(Long id, ServiceDto dto);

    void delete(Long id);

    BasicServiceResponse createBasicService(CreateBasicServiceRequest request);
}
