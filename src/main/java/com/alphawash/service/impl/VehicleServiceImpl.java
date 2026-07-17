package com.alphawash.service.impl;

import com.alphawash.constant.Constant;
import com.alphawash.constant.Size;
import com.alphawash.converter.CustomerConverter;
import com.alphawash.converter.VehicleConverter;
import com.alphawash.dto.BasicVehicleServiceUsedDto;
import com.alphawash.dto.BasicVehicleServiceUsedSearchDto;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.dto.VehicleServicesDto;
import com.alphawash.entity.Customer;
import com.alphawash.entity.MergeVehicleLog;
import com.alphawash.entity.Model;
import com.alphawash.entity.Order;
import com.alphawash.entity.OrderDetail;
import com.alphawash.entity.Vehicle;
import com.alphawash.exception.BusinessException;
import com.alphawash.exception.DuplicateVehicleException;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.repository.MergeVehicleLogRepository;
import com.alphawash.repository.ModelRepository;
import com.alphawash.repository.OrderDetailRepository;
import com.alphawash.repository.OrderRepository;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.request.MergeVehiclesRequest;
import com.alphawash.request.VehicleRequest;
import com.alphawash.response.BasicCustomerVehicleDetailResponse;
import com.alphawash.response.DuplicateVehicleGroupResponse;
import com.alphawash.response.DuplicateVehicleItemResponse;
import com.alphawash.response.MergeVehicleLogResponse;
import com.alphawash.response.VehicleMergePreviewItemResponse;
import com.alphawash.response.VehicleMergePreviewResponse;
import com.alphawash.response.VehicleMergeResponse;
import com.alphawash.response.VehiclePlateCheckResponse;
import com.alphawash.service.VehicleService;
import com.alphawash.util.LicensePlateUtil;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.StringUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;
    private final ModelRepository modelRepository;
    private final CustomerRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final MergeVehicleLogRepository mergeVehicleLogRepository;

    @Override
    public List<VehicleDto> search() {
        var result = repository.findAll();
        return VehicleConverter.INSTANCE.toDto(result);
    }

    @Override
    @Transactional
    public VehicleDto insert(VehicleRequest request) {
        String normalizedPlate = LicensePlateUtil.normalize(request.licensePlate());
        repository.findByNormalizedLicensePlateAndDeleteFlagFalse(normalizedPlate).ifPresent(existing -> {
            throw new DuplicateVehicleException(
                    "Biển số xe đã tồn tại: " + existing.getLicensePlate(), VehicleConverter.INSTANCE.toDto(existing));
        });

        Vehicle vehicle = VehicleConverter.INSTANCE.fromRequest(request);
        vehicle.setNormalizedLicensePlate(normalizedPlate);
        var savedVehicle = repository.save(vehicle);
        return VehicleConverter.INSTANCE.toDto(savedVehicle);
    }

    @Override
    public VehicleDto findById(UUID id) {
        var dto = repository.findById(id);
        return null;
    }

    @Override
    @Transactional
    public void update(VehicleRequest request) {
        var dto = repository.findById(request.id());
        dto.ifPresentOrElse(
                vehicle -> {
                    String normalizedPlate = LicensePlateUtil.normalize(request.licensePlate());
                    repository
                            .findByNormalizedLicensePlateAndDeleteFlagFalse(normalizedPlate)
                            .filter(existing -> !existing.getId().equals(vehicle.getId()))
                            .ifPresent(existing -> {
                                throw new DuplicateVehicleException(
                                        "Biển số xe đã tồn tại: " + existing.getLicensePlate(),
                                        VehicleConverter.INSTANCE.toDto(existing));
                            });

                    var updatedVehicle = VehicleConverter.INSTANCE.fromRequest(request);
                    updatedVehicle.setId(vehicle.getId());
                    updatedVehicle.setNormalizedLicensePlate(normalizedPlate);
                    repository.save(updatedVehicle);
                },
                () -> {
                    throw new IllegalArgumentException("Vehicle not found with id: " + request.id());
                });
    }

    @Override
    public VehicleDto findByLicensePlate(String licensePlate) {
        var vehicle = repository.findByLicensePlate(licensePlate);
        if (vehicle.isPresent()) {
            return VehicleConverter.INSTANCE.toDto(vehicle.get());
        } else {
            throw new IllegalArgumentException("Vehicle not found with license plate: " + licensePlate);
        }
    }

    @Override
    public List<CarSizeDto> getCarSizes() {
        return repository.findCar().stream()
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
        return repository.searchVehicleServiceUsage().stream()
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
        List<Vehicle> vehicle = repository.findByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(vehicle)) {
            List<BasicVehicleServiceUsedDto> vehicles = new ArrayList<>();
            vehicle.forEach(v -> {
                List<VehicleServicesDto> services =
                        repository.searchVehicleServiceUsageDetail(v.getLicensePlate()).stream()
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

    @Override
    public VehiclePlateCheckResponse checkPlate(String plate) {
        String normalizedPlate = LicensePlateUtil.normalize(plate);
        return repository
                .findByNormalizedLicensePlateAndDeleteFlagFalse(normalizedPlate)
                .map(vehicle -> VehiclePlateCheckResponse.builder()
                        .exists(true)
                        .vehicle(VehicleConverter.INSTANCE.toDto(vehicle))
                        .customer(vehicle.getCustomer() != null
                                ? CustomerConverter.INSTANCE.toDto(vehicle.getCustomer())
                                : null)
                        .hasCustomer(vehicle.getCustomer() != null)
                        .build())
                .orElseGet(() -> VehiclePlateCheckResponse.builder()
                        .exists(false)
                        .hasCustomer(false)
                        .build());
    }

    @Override
    @Transactional
    public VehicleDto linkCustomer(UUID vehicleId, UUID customerId) {
        Vehicle vehicle = repository
                .findById(vehicleId)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + vehicleId));

        if (vehicle.getCustomer() != null) {
            throw new BusinessException(
                    HttpStatus.CONFLICT, "Xe đã có chủ sở hữu, vui lòng dùng chức năng chuyển quyền sở hữu");
        }

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + customerId));

        vehicle.setCustomer(customer);
        return VehicleConverter.INSTANCE.toDto(repository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleDto transferOwnership(UUID vehicleId, UUID newCustomerId) {
        Vehicle vehicle = repository
                .findById(vehicleId)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + vehicleId));

        Customer newCustomer = customerRepository
                .findById(newCustomerId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + newCustomerId));

        vehicle.setCustomer(newCustomer);
        return VehicleConverter.INSTANCE.toDto(repository.save(vehicle));
    }

    @Override
    public List<DuplicateVehicleGroupResponse> findDuplicateVehicles() {
        return repository.findDuplicateNormalizedPlates().stream()
                .map(plate -> DuplicateVehicleGroupResponse.builder()
                        .normalizedPlate(plate)
                        .vehicles(repository.findAllByNormalizedLicensePlateAndDeleteFlagFalse(plate).stream()
                                .map(this::toDuplicateVehicleItem)
                                .toList())
                        .build())
                .toList();
    }

    private DuplicateVehicleItemResponse toDuplicateVehicleItem(Vehicle vehicle) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByVehicle_IdAndDeleteFlagFalse(vehicle.getId());
        var lastServiceDate = orderDetails.stream()
                .map(od -> od.getOrder() != null ? od.getOrder().getDate() : null)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return DuplicateVehicleItemResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .customer(vehicle.getCustomer() != null ? CustomerConverter.INSTANCE.toDto(vehicle.getCustomer()) : null)
                .orderCount(orderDetails.size())
                .lastServiceDate(lastServiceDate)
                .build();
    }

    @Override
    @Transactional
    public VehicleMergeResponse mergeVehicles(MergeVehiclesRequest request) {
        if (CollectionUtils.isEmpty(request.duplicateVehicleIds())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Danh sách xe trùng không được để trống");
        }

        Vehicle primary = repository
                .findById(request.primaryVehicleId())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy xe chính với id: " + request.primaryVehicleId()));

        Customer primaryCustomer = customerRepository
                .findById(request.primaryCustomerId())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng chính với id: " + request.primaryCustomerId()));

        if (primary.getCustomer() == null
                || !primary.getCustomer().getId().equals(primaryCustomer.getId())) {
            primary.setCustomer(primaryCustomer);
            primary = repository.save(primary);
        }

        List<UUID> archivedVehicleIds = new ArrayList<>();
        List<String> duplicateVehiclePlates = new ArrayList<>();
        Set<UUID> duplicateCustomerIds = new LinkedHashSet<>();
        int orderDetailsMigrated = 0;
        int ordersMigrated = 0;

        for (UUID duplicateId : request.duplicateVehicleIds()) {
            if (duplicateId.equals(primary.getId())) {
                continue;
            }

            Vehicle duplicate = repository
                    .findById(duplicateId)
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe trùng với id: " + duplicateId));

            // Dùng native SQL với UUID để tránh entity-state issues sau clearAutomatically
            orderDetailsMigrated += orderDetailRepository.reassignVehicle(duplicateId, primary.getId());

            // Migrate orders theo vehicle (qua order_detail subquery) - đúng hơn migrate theo customer,
            // vì xử lý được cả orders có customer_id = NULL hoặc customer khác với vehicle.customer
            ordersMigrated += orderRepository.reassignCustomerByVehicle(duplicateId, primaryCustomer.getId());

            if (duplicate.getCustomer() != null
                    && !duplicate.getCustomer().getId().equals(primaryCustomer.getId())) {
                duplicateCustomerIds.add(duplicate.getCustomer().getId());
            }

            duplicateVehiclePlates.add(duplicate.getLicensePlate());
            duplicate.setDeleteFlag(true);
            repository.save(duplicate);
            archivedVehicleIds.add(duplicateId);
        }

        List<UUID> archivedCustomerIds = new ArrayList<>();
        List<String> duplicateCustomerNames = new ArrayList<>();

        for (UUID duplicateCustomerId : duplicateCustomerIds) {
            Customer duplicateCustomer =
                    customerRepository.findById(duplicateCustomerId).orElse(null);
            if (duplicateCustomer == null) {
                continue;
            }
            duplicateCustomerNames.add(duplicateCustomer.getCustomerName());

            // Không cần reassignCustomer nữa — đã migrate orders theo vehicle ở loop trên
            if (CollectionUtils.isEmpty(repository.findByCustomerIdAndDeleteFlagFalse(duplicateCustomerId))) {
                duplicateCustomer.setDeleteFlag(true);
                customerRepository.save(duplicateCustomer);
                archivedCustomerIds.add(duplicateCustomerId);
            }
        }

        MergeVehicleLog log = mergeVehicleLogRepository.save(MergeVehicleLog.builder()
                .mergeDate(LocalDateTime.now())
                .operatorUsername(currentUsername())
                .primaryCustomerId(primaryCustomer.getId())
                .primaryCustomerName(primaryCustomer.getCustomerName())
                .primaryVehicleId(primary.getId())
                .primaryVehicleLicensePlate(primary.getLicensePlate())
                .duplicateCustomerIds(joinIds(duplicateCustomerIds))
                .duplicateCustomerNames(String.join(", ", duplicateCustomerNames))
                .duplicateVehicleLicensePlates(String.join(", ", duplicateVehiclePlates))
                .ordersMigrated(ordersMigrated)
                .orderDetailsMigrated(orderDetailsMigrated)
                .customersArchived(archivedCustomerIds.size())
                .status("SUCCESS")
                .build());

        return VehicleMergeResponse.builder()
                .logId(log.getId())
                .primaryVehicleId(primary.getId())
                .archivedVehicleIds(archivedVehicleIds)
                .archivedCustomerIds(archivedCustomerIds)
                .ordersMigrated(ordersMigrated)
                .orderDetailsMigrated(orderDetailsMigrated)
                .primaryVehicle(VehicleConverter.INSTANCE.toDto(primary))
                .primaryCustomer(CustomerConverter.INSTANCE.toDto(primaryCustomer))
                .build();
    }

    @Override
    public VehicleMergePreviewResponse previewMerge(List<UUID> vehicleIds) {
        if (CollectionUtils.isEmpty(vehicleIds)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Danh sách xe không được để trống");
        }

        List<VehicleMergePreviewItemResponse> items = new ArrayList<>();
        int totalOrders = 0;
        BigDecimal totalSpending = BigDecimal.ZERO;

        for (UUID vehicleId : vehicleIds) {
            Vehicle vehicle = repository
                    .findById(vehicleId)
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy xe với id: " + vehicleId));

            List<OrderDetail> orderDetails = orderDetailRepository.findAllByVehicle_IdAndDeleteFlagFalse(vehicleId);
            Map<UUID, Order> distinctOrders = orderDetails.stream()
                    .map(OrderDetail::getOrder)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Order::getId, order -> order, (a, b) -> a));

            BigDecimal vehicleSpending = distinctOrders.values().stream()
                    .map(Order::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            LocalDateTime lastVisitDate = distinctOrders.values().stream()
                    .map(Order::getDate)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            items.add(VehicleMergePreviewItemResponse.builder()
                    .vehicleId(vehicle.getId())
                    .licensePlate(vehicle.getLicensePlate())
                    .customer(vehicle.getCustomer() != null
                            ? CustomerConverter.INSTANCE.toDto(vehicle.getCustomer())
                            : null)
                    .orderCount(distinctOrders.size())
                    .totalSpending(vehicleSpending)
                    .lastVisitDate(lastVisitDate)
                    .build());

            totalOrders += distinctOrders.size();
            totalSpending = totalSpending.add(vehicleSpending);
        }

        return VehicleMergePreviewResponse.builder()
                .vehicles(items)
                .totalOrders(totalOrders)
                .totalSpending(totalSpending)
                .build();
    }

    @Override
    public List<MergeVehicleLogResponse> getMergeLogs() {
        return mergeVehicleLogRepository.findAllByOrderByMergeDateDesc().stream()
                .map(log -> MergeVehicleLogResponse.builder()
                        .id(log.getId())
                        .mergeDate(log.getMergeDate())
                        .operatorUsername(log.getOperatorUsername())
                        .primaryCustomerId(log.getPrimaryCustomerId())
                        .primaryCustomerName(log.getPrimaryCustomerName())
                        .primaryVehicleId(log.getPrimaryVehicleId())
                        .primaryVehicleLicensePlate(log.getPrimaryVehicleLicensePlate())
                        .duplicateCustomerIds(log.getDuplicateCustomerIds())
                        .duplicateCustomerNames(log.getDuplicateCustomerNames())
                        .duplicateVehicleLicensePlates(log.getDuplicateVehicleLicensePlates())
                        .ordersMigrated(log.getOrdersMigrated())
                        .orderDetailsMigrated(log.getOrderDetailsMigrated())
                        .customersArchived(log.getCustomersArchived())
                        .status(log.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : Constant.ADMINISTRATOR;
    }

    private String joinIds(Set<UUID> ids) {
        return ids.stream().map(UUID::toString).collect(Collectors.joining(", "));
    }
}
