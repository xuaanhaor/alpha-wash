package com.alphawash.service.impl;

import com.alphawash.constant.SeqCode;
import com.alphawash.constant.Size;
import com.alphawash.converter.ServiceConverter;
import com.alphawash.dto.ServiceDto;
import com.alphawash.entity.Service;
import com.alphawash.entity.ServiceCatalog;
import com.alphawash.entity.ServiceType;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.ServiceCatalogRepository;
import com.alphawash.repository.ServiceRepository;
import com.alphawash.repository.ServiceTypeRepository;
import com.alphawash.request.CreateBasicServiceRequest;
import com.alphawash.request.UpdateBasicServiceRequest;
import com.alphawash.response.BasicServiceResponse;
import com.alphawash.response.UpdateServiceResponse;
import com.alphawash.service.GenerateSeqService;
import com.alphawash.service.ServiceService;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
import com.alphawash.util.StringUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceConverter converter = ServiceConverter.INSTANCE;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final GenerateSeqService generateSeqService;

    @Override
    public List<BasicServiceResponse> getAllBasicServices() {
        return serviceRepository.getBasicServices();
    }

    @Override
    public List<ServiceDto> getAll() {
        List<Service> services = serviceRepository.getServicesAvailable();
        return converter.toDto(services);
    }

    @Override
    public ServiceDto getById(Long id) {
        return serviceRepository.findById(id).map(converter::toDto).orElse(null);
    }

    @Override
    public ServiceDto create(ServiceDto dto) {
        Service entity = converter.toEntity(dto);
        entity.setServiceType(
                serviceTypeRepository.findByCode(dto.getServiceTypeCode()).orElseThrow());
        return converter.toDto(serviceRepository.save(entity));
    }

    @Override
    public ServiceDto update(Long id, ServiceDto patchData) {
        return serviceRepository
                .findById(id)
                .map(existing -> {
                    ServiceDto currentDto = converter.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    Service updatedEntity = converter.toEntity(currentDto);
                    updatedEntity.setServiceType(serviceTypeRepository
                            .findByCode(currentDto.getServiceTypeCode())
                            .orElse(null));

                    return converter.toDto(serviceRepository.save(updatedEntity));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BasicServiceResponse createBasicService(CreateBasicServiceRequest request) {
        isValidRequest(request);
        ServiceType serviceTypeOptional = serviceTypeRepository
                .findByCode(request.serviceTypeCode())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST, "Không tìm thấy loại dịch vụ: " + request.serviceTypeCode()));

        String seqCode = generateSeqService.generateSeqCode(SeqCode.SERVICE);
        Service service = serviceRepository.save(Service.builder()
                .code(seqCode)
                .serviceType(serviceTypeOptional)
                .serviceName(request.serviceName())
                .duration(request.duration())
                .note(request.note())
                .build());

        String seqServiceCatalogCode = generateSeqService.generateSeqCode(SeqCode.SERVICE_CATALOG);
        ServiceCatalog serviceCatalog = serviceCatalogRepository.save(ServiceCatalog.builder()
                .code(seqServiceCatalogCode)
                .service(service)
                .price(request.price())
                .size(Size.valueOf(request.size()))
                .build());

        return BasicServiceResponse.builder()
                .serviceTypeCode(serviceTypeOptional.getCode())
                .serviceTypeName(service.getServiceName())
                .serviceCode(service.getCode())
                .serviceName(service.getServiceName())
                .serviceCatalogCode(serviceCatalog.getCode())
                .price(serviceCatalog.getPrice())
                .duration(service.getDuration())
                .size(serviceCatalog.getSize().getValue())
                .note(service.getNote())
                .build();
    }

    @Override
    @Transactional
    public UpdateServiceResponse updateBasicService(UpdateBasicServiceRequest request) {
        if (ObjectUtils.isNull(request)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Request cannot null");
        }

        // 1. Lấy service chính
        Service service = serviceRepository
                .findByCode(request.serviceCode())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "Service not found: " + request.serviceCode()));
        // Lây service type nếu có thay đổi
        if (StringUtils.isNotNullOrBlank(request.serviceTypeCode())
                && !request.serviceTypeCode().equals(service.getServiceType().getCode())) {
            ServiceType serviceType = serviceTypeRepository
                    .findByCode(request.serviceTypeCode())
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.BAD_REQUEST, "Service type not found: " + request.serviceTypeCode()));
            service.setServiceType(serviceType);
        }
        // Update thông tin chung của service
        ObjectUtils.setIfNotNull(request.serviceName(), service::setServiceName);
        ObjectUtils.setIfNotNull(request.duration(), service::setDuration);
        ObjectUtils.setIfNotNull(request.note(), service::setNote);
        serviceRepository.save(service);

        // 2. Lấy danh sách catalog theo serviceCode
        List<ServiceCatalog> catalogs = serviceCatalogRepository.findByServiceCode(request.serviceCode());

        // 3. Update hoặc tạo mới giá theo từng size trong request
        if (request.sizes() != null) {
            request.sizes().forEach((sizeKey, sizeRequest) -> {
                ServiceCatalog catalog = catalogs.stream()
                        .filter(c -> c.getSize().name().equalsIgnoreCase(sizeKey))
                        .findFirst()
                        .orElseGet(() -> {
                            // Nếu chưa có thì tạo mới
                            String seqServiceCatalogCode = generateSeqService.generateSeqCode(SeqCode.SERVICE_CATALOG);
                            ServiceCatalog newCatalog = ServiceCatalog.builder()
                                    .code(seqServiceCatalogCode)
                                    .service(service)
                                    .price(sizeRequest.price())
                                    .size(Size.valueOf(sizeKey.toUpperCase()))
                                    .build();
                            catalogs.add(newCatalog);
                            return newCatalog;
                        });

                // Update giá nếu có
                if (sizeRequest.price() != null) {
                    catalog.setPrice(sizeRequest.price());
                }

                serviceCatalogRepository.save(catalog);
            });
        }

        // 4. Trả về response
        return UpdateServiceResponse.builder()
                .serviceCode(service.getCode())
                .serviceName(service.getServiceName())
                .duration(service.getDuration())
                .note(service.getNote())
                .catalogs(catalogs.stream()
                        .map(c -> new UpdateServiceResponse.CatalogInfo(
                                c.getCode(), c.getSize().getValue(), c.getPrice()))
                        .toList())
                .build();
    }

    private static void isValidRequest(CreateBasicServiceRequest request) {
        if (ObjectUtils.isNull(request)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Thông tin tạo mới không được để trống!");
        }
        if (StringUtils.isNullOrBlank(request.serviceName())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tên dịch vụ không được để trống!");
        }
        if (StringUtils.isNullOrBlank(request.serviceTypeCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Mã dịch vụ rỗng");
        }
    }
}
