package com.alphawash.service.impl;

import com.alphawash.entity.ServiceCategoryEntity;
import com.alphawash.repository.ServiceCategoryRepository;
import com.alphawash.repository.ServiceItemRepository;
import com.alphawash.request.ServiceCategoryRequest;
import com.alphawash.response.ServiceCategoryResponse;
import com.alphawash.service.ServiceCategoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository repository;
    private final ServiceItemRepository serviceItemRepository;

    @Override
    public List<ServiceCategoryResponse> getAll(Boolean activeOnly) {
        List<ServiceCategoryEntity> list = (activeOnly != null && activeOnly)
                ? repository.findAllByActiveTrueAndDeleteFlagFalseOrderBySortOrderAsc()
                : repository.findAllByDeleteFlagFalseOrderBySortOrderAsc();
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public ServiceCategoryResponse getById(UUID id) {
        return repository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleteFlag()))
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + id));
    }

    @Override
    @Transactional
    public ServiceCategoryResponse create(ServiceCategoryRequest request) {
        String code = request.code() != null ? request.code().toUpperCase().trim() : null;
        if (code == null || code.isBlank()) {
            throw new RuntimeException("Code danh mục không được để trống");
        }
        if (repository.existsByCodeAndDeleteFlagFalse(code)) {
            throw new RuntimeException("Code '" + code + "' đã tồn tại");
        }
        ServiceCategoryEntity entity = ServiceCategoryEntity.builder()
                .code(code)
                .name(request.name())
                .description(request.description())
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .active(request.active() != null ? request.active() : true)
                .build();
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public ServiceCategoryResponse update(UUID id, ServiceCategoryRequest request) {
        ServiceCategoryEntity entity = repository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleteFlag()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + id));

        if (request.code() != null) {
            String code = request.code().toUpperCase().trim();
            if (!code.equals(entity.getCode()) && repository.existsByCodeAndDeleteFlagFalseAndIdNot(code, id)) {
                throw new RuntimeException("Code '" + code + "' đã tồn tại");
            }
            entity.setCode(code);
        }
        if (request.name() != null) entity.setName(request.name());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.sortOrder() != null) entity.setSortOrder(request.sortOrder());
        if (request.active() != null) entity.setActive(request.active());

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ServiceCategoryEntity entity = repository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleteFlag()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + id));

        // Kiểm tra xem có service nào đang dùng category code này không
        long usageCount = serviceItemRepository
                .findByDeleteFlagFalseOrderByCategoryAscSortOrderAsc()
                .stream()
                .filter(s -> entity.getCode().equals(s.getCategory() != null ? s.getCategory().name() : null)
                        || entity.getCode().equals(s.getCategoryCode()))
                .count();

        if (usageCount > 0) {
            throw new RuntimeException(
                    "Không thể xóa danh mục '" + entity.getName() + "' vì đang có " + usageCount + " dịch vụ sử dụng");
        }

        entity.setDeleteFlag(true);
        repository.save(entity);
    }

    private ServiceCategoryResponse toResponse(ServiceCategoryEntity e) {
        return new ServiceCategoryResponse(
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getDescription(),
                e.getSortOrder(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
