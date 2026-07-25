package com.alphawash.service;

import com.alphawash.request.ServiceCategoryRequest;
import com.alphawash.response.ServiceCategoryResponse;
import java.util.List;
import java.util.UUID;

public interface ServiceCategoryService {

    List<ServiceCategoryResponse> getAll(Boolean activeOnly);

    ServiceCategoryResponse getById(UUID id);

    ServiceCategoryResponse create(ServiceCategoryRequest request);

    ServiceCategoryResponse update(UUID id, ServiceCategoryRequest request);

    void delete(UUID id);
}
