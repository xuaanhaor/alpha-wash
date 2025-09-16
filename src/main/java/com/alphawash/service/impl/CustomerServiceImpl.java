package com.alphawash.service.impl;

import com.alphawash.constant.RegexConstant;
import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.CustomerVehicleDto;
import com.alphawash.dto.CustomerVehicleFlatDto;
import com.alphawash.entity.Customer;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.request.CustomerRequest;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.service.CustomerService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
import com.alphawash.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDto> getAll() {
        return CustomerConverter.INSTANCE.toDto(customerRepository.findAll());
    }

    @Override
    public CustomerDto getById(UUID id) {
        return customerRepository
                .findById(id)
                .map(CustomerConverter.INSTANCE::toDto)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với ID: " + id));
    }

    @Override
    public CustomerDto create(CustomerRequest request) {
        // Kiểm tra xem khách hàng đã tồn tại hay chưa
        if (customerRepository.findByPhone(request.phone()).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT, "Khách hàng đã tồn tại trong hệ thống: " + request.phone());
        }
        Customer saved = customerRepository.save(Customer.builder()
                .customerName(request.customerName())
                .phone(request.phone())
                .note(request.note())
                .build());
        return CustomerConverter.INSTANCE.toDto(saved);
    }

    @Override
    public CustomerDto update(UUID id, CustomerDto patchData) {
        return customerRepository
                .findById(id)
                .map(existing -> {
                    CustomerDto currentDto = CustomerConverter.INSTANCE.toDto(existing);

                    // Kiểm tra trùng tên (nếu tên thay đổi)
                    if (patchData.getCustomerName() != null
                            && !patchData.getCustomerName().equalsIgnoreCase(existing.getCustomerName())) {

                        customerRepository
                                .findByCustomerNameAndDeleteFlagFalse(patchData.getCustomerName())
                                .ifPresent(duplicate -> {
                                    throw new BusinessException(
                                            HttpStatus.CONFLICT,
                                            "Tên khách hàng đã tồn tại: " + patchData.getCustomerName());
                                });
                    }

                    // Kiểm tra trùng số điện thoại (nếu thay đổi)
                    if (patchData.getPhone() != null && !patchData.getPhone().equals(existing.getPhone())) {

                        customerRepository.findByPhone(patchData.getPhone()).ifPresent(duplicate -> {
                            throw new BusinessException(
                                    HttpStatus.CONFLICT, "Số điện thoại đã tồn tại: " + patchData.getPhone());
                        });
                    }

                    // Áp dụng patch
                    PatchHelper.applyPatch(patchData, currentDto);
                    existing.setCustomerName(currentDto.getCustomerName());
                    existing.setPhone(currentDto.getPhone());
                    existing.setNote(currentDto.getNote());

                    return CustomerConverter.INSTANCE.toDto(customerRepository.save(existing));
                })
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        customerRepository
                .findById(id)
                .ifPresentOrElse(
                        customer -> {
                            customer.setDeleteFlag(true);
                            customerRepository.save(customer);
                        },
                        () -> {
                            throw new BusinessException(
                                    HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng để xóa với ID: " + id);
                        });
    }

    //    @Override
    //    public CustomerVehicleResponse findByPhone(String phone) {
    //        Optional<Customer> result = customerRepository.findByPhone(phone);
    //        return result.map(CustomerConverter.INSTANCE::toCustomerVehicleResp).orElse(null);
    //    }

    @Override
    public List<CustomerVehicleResponse> findCustomerVehicleByPhoneOrLicensePlate(String phoneOrLicensePlate) {
        if (StringUtils.isNullOrBlank(phoneOrLicensePlate)) {
            return Collections.emptyList();
        }

        // Trim and normalize input
        String searchTerm = phoneOrLicensePlate.trim().toUpperCase();
        List<CustomerVehicleFlatDto> flatList = new ArrayList<>();

        // Determine search type and execute appropriate search
        if (searchTerm.matches(RegexConstant.PHONE_REGEX)) {
            // Exact phone search
            flatList = customerRepository.findCustomerWithVehicleByPhone(searchTerm);
        } else if (searchTerm.matches(RegexConstant.LICENSE_PLATE_REGEX)) {
            // Exact license plate search
            flatList = customerRepository.findCustomerWithVehicleByLicensePlate(searchTerm);
        } else {
            // Fuzzy search for partial matches
            if (searchTerm.startsWith("0") && searchTerm.matches("^0\\d*$")) {
                // Partial phone number (starts with 0, only digits)
                flatList = customerRepository.findCustomerWithVehicleByPhoneLike(searchTerm + "%");
            } else if (searchTerm.matches("^\\d.*[A-Z].*") || searchTerm.matches("^\\d{1,2}[A-Z].*")) {
                // Partial license plate (starts with digits, contains letters)
                flatList = customerRepository.findCustomerWithVehicleByLicensePlateLike(searchTerm + "%");
            } else {
                // Fallback: search both phone and license plate with LIKE
                List<CustomerVehicleFlatDto> phoneResults =
                        customerRepository.findCustomerWithVehicleByPhoneLike(searchTerm + "%");
                List<CustomerVehicleFlatDto> plateResults =
                        customerRepository.findCustomerWithVehicleByLicensePlateLike(searchTerm + "%");

                flatList = new ArrayList<>();
                flatList.addAll(phoneResults);
                flatList.addAll(plateResults);
                flatList = new ArrayList<>(flatList.stream()
                        .collect(Collectors.toMap(
                                dto -> dto.getId() + "_" + dto.getLicensePlate(),
                                dto -> dto,
                                (existing, replacement) -> existing))
                        .values());
            }
        }

        if (CollectionUtils.isEmpty(flatList)) {
            return Collections.emptyList();
        }

        // Group by customer ID and build response list
        Map<UUID, List<CustomerVehicleFlatDto>> groupedByCustomer =
                flatList.stream().collect(Collectors.groupingBy(CustomerVehicleFlatDto::getId));

        return groupedByCustomer.values().stream()
                .map(customerData -> {
                    CustomerVehicleFlatDto first = customerData.get(0);

                    List<CustomerVehicleDto> vehicles = customerData.stream()
                            .filter(item -> ObjectUtils.isNotNull(item.getLicensePlate()))
                            .map(flat -> new CustomerVehicleDto(
                                    flat.getBrandCode(),
                                    flat.getBrandName(),
                                    flat.getModelCode(),
                                    flat.getModelName(),
                                    flat.getLicensePlate()))
                            .distinct()
                            .collect(Collectors.toList());

                    return CustomerVehicleResponse.builder()
                            .id(first.getId())
                            .name(first.getCustomerName())
                            .phone(first.getPhone())
                            .vehicles(vehicles)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
