package com.alphawash.service;

import com.alphawash.constant.ServiceCategory;
import com.alphawash.request.ServiceItemRequest;
import com.alphawash.response.ServiceItemResponse;
import java.util.List;
import java.util.UUID;

public interface CatalogService {

    /** Danh sách dịch vụ, filter theo category và active */
    List<ServiceItemResponse> getAll(ServiceCategory category, boolean activeOnly);

    /** Danh sách categories đang có dữ liệu */
    List<ServiceCategory> getCategories();

    /** Chỉ lấy các dịch vụ có thể tặng kèm */
    List<ServiceItemResponse> getBonusServices();

    ServiceItemResponse getById(UUID id);

    ServiceItemResponse create(ServiceItemRequest request);

    ServiceItemResponse update(UUID id, ServiceItemRequest request);

    /** Soft delete (toggle active = false + deleteFlag = true) */
    void delete(UUID id);

    /**
     * Backfill category_code cho các service cũ có categoryCode = null.
     * Với service có category == OTHER → dùng category.name() làm code tạm thời.
     * Với service có category != null → set categoryCode = category.name().
     * Trả về số bản ghi đã cập nhật.
     */
    int backfillCategoryCode();
}
