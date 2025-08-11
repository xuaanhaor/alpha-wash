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
        List<Service> services = serviceRepository.findAll();
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
    public BasicServiceResponse updateBasicService(UpdateBasicServiceRequest request) {
        if (ObjectUtils.isNull(request)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Request cannot null");
        }
        BasicServiceResponse serviceResponse = serviceRepository
                .getBasicServiceByServiceCode(request.serviceCode())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "Service not found: " + request.serviceCode()));
        ServiceCatalog serviceCatalog = serviceCatalogRepository
                .findByCode(serviceResponse.getServiceCatalogCode())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Error when tried to get service catalog: " + serviceResponse.getServiceCatalogCode()));
        Service service = serviceRepository
                .findByCode(request.serviceCode())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST, "Error when trying to get service: " + request.serviceCode()));

        ObjectUtils.setIfNotNull(request.serviceName(), service::setServiceName);
        ObjectUtils.setIfNotNull(request.duration(), service::setDuration);
        ObjectUtils.setIfNotNull(request.note(), service::setNote);

        if (StringUtils.isNotNullOrBlank(request.size())) {
            serviceCatalog.setSize(Size.valueOf(request.size()));
            serviceCatalogRepository.save(serviceCatalog);
        }
        if (request.price() != null) {
            serviceCatalog.setPrice(request.price());
            serviceCatalogRepository.save(serviceCatalog);
        }

        serviceRepository.save(service);

        return BasicServiceResponse.builder()
                .serviceTypeCode(serviceResponse.getServiceTypeCode())
                .serviceTypeName(serviceResponse.getServiceTypeName())
                .serviceCode(service.getCode())
                .serviceName(service.getServiceName())
                .serviceCatalogCode(serviceCatalog.getCode())
                .price(serviceCatalog.getPrice())
                .duration(service.getDuration())
                .size(serviceCatalog.getSize().getValue())
                .note(service.getNote())
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
