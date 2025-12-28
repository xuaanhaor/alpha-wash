package com.alphawash.service.impl;

import com.alphawash.constant.Size;
import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.BasicVehicleServiceUsedDto;
import com.alphawash.dto.BasicVehicleServiceUsedSearchDto;
import com.alphawash.dto.BrandDto;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.ModelDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.dto.VehicleServicesDto;
import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.Model;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.BrandRepository;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.request.VehicleRequest;
import com.alphawash.response.BasicCustomerVehicleDetailResponse;
import com.alphawash.service.VehicleService;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ModelRepository modelRepository;
    private final CustomerRepository customerRepository;
    private final BrandRepository brandRepository;


    @Override
    public List<VehicleDto> search() {
        var result = vehicleRepository.findAll();
        return VehicleConverter.toDtoList(result);
    }

    @Override
    public VehicleDto findById(UUID id) {
        var dto = vehicleRepository.findById(id);
        return null;
    }

    @Override
    @Transactional
    public VehicleDto create(VehicleDto dto) {
        // 1. Kiểm tra dữ liệu
        if (StringUtils.isNullOrBlank(dto.getLicensePlate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        if (ObjectUtils.isNull(dto.getBrand()) || StringUtils.isNullOrBlank(dto.getBrand().getBrandCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "BrandCode không được để trống");
        }
        if (ObjectUtils.isNull(dto.getModel()) || StringUtils.isNullOrBlank(dto.getModel().getModelCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ModelCode không được để trống");
        }

        // 2. Check trùng biển số
        if (vehicleRepository.existsByLicensePlate(dto.getLicensePlate().trim())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe đã tồn tại");
        }

        // 3. Xử lý khách hàng (optional)
        Customer customer = null;
        if (StringUtils.isNullOrBlank(String.valueOf(dto.getCustomerId()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Customer ID không được để trống");
        } else {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }

        // 4. Xử lý Brand
        Brand brand = brandRepository.findByCode(dto.getBrand().getBrandCode())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy brand với code: "
                                + dto.getBrand().getBrandCode()));

        // 5. Xử lý Model (và check đúng brand)
        Model model = modelRepository.findByCode(dto.getModel().getModelCode())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy model với code: "
                                + dto.getModel().getModelCode()));

        if (!model.getBrand().getCode().equals(brand.getCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Model không thuộc brand đã chọn");
        }

        // 6. Tạo Vehicle entity
        Vehicle vehicle = Vehicle.builder()
                .licensePlate(dto.getLicensePlate().trim())
                .customer(customer)
                .brand(brand)
                .model(model)
                .imageUrl(dto.getImageUrl())
                .note(dto.getNote())
                .build();

        // 7. Tạo mới
        Vehicle saved = vehicleRepository.save(vehicle);

        // 8. Return DTO
        return VehicleDto.builder()
                .vehicleId(saved.getId())
                .customerId(customer != null ? customer.getId() : null)
                .licensePlate(saved.getLicensePlate())
                .brand(BrandDto.builder()
                        .brandCode(brand.getCode())
                        .brandName(brand.getBrandName())
                        .build())
                .model(ModelDto.builder()
                        .modelCode(model.getCode())
                        .modelName(model.getModelName())
                        .size(model.getSize().name())
                        .build())
                .imageUrl(saved.getImageUrl())
                .note(saved.getNote())
                .build();
    }

    @Override
    @Transactional
    public VehicleDto update( VehicleDto dto) {

        // 0. Validate vehicleId
        if (StringUtils.isNullOrBlank(String.valueOf(dto.getVehicleId()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VehicleId không được để trống");
        }

        // 1. Tìm vehicle hiện tại
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy xe với id: " + dto.getVehicleId()));

        // 2. Validate input cơ bản
        if (StringUtils.isNullOrBlank(dto.getLicensePlate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }
        if (ObjectUtils.isNull(dto.getBrand()) || StringUtils.isNullOrBlank(dto.getBrand().getBrandCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "BrandCode không được để trống");
        }
        if (ObjectUtils.isNull(dto.getModel()) || StringUtils.isNullOrBlank(dto.getModel().getModelCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ModelCode không được để trống");
        }

        String newPlate = dto.getLicensePlate().trim();

        // 3. Check trùng biển số (loại trừ chính xe đang update)
        if (vehicleRepository.existsByLicensePlateAndIdNot(newPlate, dto.getVehicleId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe đã tồn tại");
        }

        // 4. Xử lý customer (optional)
        Customer customer = null;
        if (StringUtils.isNullOrBlank(String.valueOf(dto.getCustomerId()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Customer ID không được để trống");
        } else {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Khách hàng không tồn tại"));
        }

        // 5. Xử lý Brand
        Brand brand = brandRepository.findByCode(dto.getBrand().getBrandCode().trim())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy brand với code: " + dto.getBrand().getBrandCode()
                ));

        // 6. Xử lý Model (và check đúng brand)
        Model model = modelRepository.findByCode(dto.getModel().getModelCode().trim())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy model với code: " + dto.getModel().getModelCode()
                ));

        if (model.getBrand() == null || !model.getBrand().getCode().equals(brand.getCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Model không thuộc brand đã chọn");
        }

        // 7. Update entity (không tạo mới)
        vehicle.setLicensePlate(newPlate);
        vehicle.setCustomer(customer);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setImageUrl(dto.getImageUrl());
        vehicle.setNote(dto.getNote());

        // 8. Save
        Vehicle saved = vehicleRepository.save(vehicle);

        // 9. Return DTO
        return VehicleDto.builder()
                .vehicleId(saved.getId())
                .customerId(saved.getCustomer() != null ? saved.getCustomer().getId() : null)
                .licensePlate(saved.getLicensePlate())
                .brand(BrandDto.builder()
                        .brandCode(saved.getBrand().getCode())
                        .brandName(saved.getBrand().getBrandName())
                        .build())
                .model(ModelDto.builder()
                        .modelCode(saved.getModel().getCode())
                        .modelName(saved.getModel().getModelName())
                        .size(saved.getModel().getSize().name())
                        .build())
                .imageUrl(saved.getImageUrl())
                .note(saved.getNote())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDto findByLicensePlate(String licensePlate) {
        //1. Kiểm tra input
        if (StringUtils.isNullOrBlank(licensePlate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Biển số xe không được để trống");
        }

        //2. Lấy dữ liệu từ DB
        Vehicle vehicle = vehicleRepository
                .findByLicensePlate(licensePlate.trim())
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "Không tìm thấy xe với biển số: " + licensePlate));

        //3. Trả KQ
        return VehicleDto.builder()
                .vehicleId(vehicle.getId())
                .customerId(
                        vehicle.getCustomer() != null
                                ? vehicle.getCustomer().getId()
                                : null
                )
                .licensePlate(vehicle.getLicensePlate())

                .brand(BrandDto.builder()
                        .brandCode(vehicle.getBrand().getCode())
                        .brandName(vehicle.getBrand().getBrandName())
                        .build())

                .model(ModelDto.builder()
                        .modelCode(vehicle.getModel().getCode())
                        .modelName(vehicle.getModel().getModelName())
                        .size(vehicle.getModel().getSize().name())
                        .build())

                .imageUrl(vehicle.getImageUrl())
                .note(vehicle.getNote())
                .build();
    }

    @Override
    public List<CarSizeDto> getCarSizes() {
        return vehicleRepository.findCar().stream()
                .map(row -> {
                    CarSizeDto dto = new CarSizeDto();
                    dto.setBrandCode((String) row[0]);
                    dto.setModelCode((String) row[1]);
                    dto.setBrandName((String) row[2]);
                    dto.setModelName((String) row[3]);
                    dto.setSize(Size.valueOf((String) row[4]));
                    dto.setNote((String) row[5]);
                    return dto;
                })
                .toList();
    }

    @Override
    public CarSizeDto updateCarSize(BasicCarSizeRequest request) {
        String modelCode = request.modelCode();
        Model model;

        if (StringUtils.isNullOrBlank(request.modelCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Mã model không được để rỗng!");
        } else {
            model = modelRepository
                    .findByCode(modelCode)
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Model xe không tồn tại với mã: " + modelCode));
        }

        Size size = Size.fromString(request.size());
        model.setSize(size);
        ObjectUtils.setIfNotNull(request.note(), model::setNote);
        modelRepository.save(model);
        return CarSizeDto.builder()
                .brandCode(model.getBrand().getCode())
                .modelCode(model.getCode())
                .brandName(model.getBrand().getBrandName())
                .modelName(model.getModelName())
                .size(size)
                .note(model.getNote())
                .build();
    }

    @Override
    public List<BasicVehicleServiceUsedSearchDto> searchVehicleServiceUsage() {
        return vehicleRepository.searchVehicleServiceUsage().stream()
                .map(row -> {
                    BasicVehicleServiceUsedSearchDto dto = BasicVehicleServiceUsedSearchDto.builder()
                            .id((Integer) row[0])
                            .licensePlate((String) row[1])
                            .vehicleName((String) row[2])
                            .customerName((String) row[3])
                            .customerId((UUID) row[4])
                            .phone((String) row[5])
                            .serviceUsage((Integer) row[6])
                            .note((String) row[7])
                            .build();
                    return dto;
                })
                .toList();
    }

    @Override
    public BasicCustomerVehicleDetailResponse searchVehicleServiceUsageDetail(UUID customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Khách hàng không tồn tại với id: " + customerId));
        List<Vehicle> vehicle = vehicleRepository.findByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(vehicle)) {
            List<BasicVehicleServiceUsedDto> vehicles = new ArrayList<>();
            vehicle.forEach(v -> {
                List<VehicleServicesDto> services =
                        vehicleRepository.searchVehicleServiceUsageDetail(v.getLicensePlate()).stream()
                                .map(row -> VehicleServicesDto.builder()
                                        .id((Integer) row[0])
                                        .serviceName((String) row[1])
                                        .checkinTime(((Timestamp) row[2])
                                                .toLocalDateTime()
                                                .toLocalDate())
                                        .build())
                                .toList();
                vehicles.add(BasicVehicleServiceUsedDto.builder()
                        .licensePlate(v.getLicensePlate())
                        .vehicleName(getVehicleName(v))
                        .services(services)
                        .build());
            });
            return BasicCustomerVehicleDetailResponse.builder()
                    .customerId(customer.getId())
                    .customerName(customer.getCustomerName())
                    .phone(customer.getPhone())
                    .vehicles(vehicles)
                    .build();
        }
        return null;
    }

    private String getVehicleName(Vehicle vehicle) {
        var brand = vehicle.getModel().getBrand().getBrandName();
        var model = vehicle.getModel().getModelName();
        return brand + " " + model;
    }
}
