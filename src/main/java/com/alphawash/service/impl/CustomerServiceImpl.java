package com.alphawash.service.impl;

import com.alphawash.constant.RegexConstant;
import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.CustomerVehicleDto;
import com.alphawash.dto.CustomerVehicleFlatDto;
import com.alphawash.entity.Customer;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.service.CustomerService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public CustomerDto create(CustomerDto dto) {
        // Kiểm tra xem tên khách hàng có tồn tại hay không
        if (customerRepository
                .findByCustomerNameAndDeleteFlagFalse(dto.getCustomerName())
                .isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT, "Tên khách hàng đã tồn tại trong hệ thống: " + dto.getCustomerName());
        }
        // Kiểm tra xem khách hàng đã tồn tại hay chưa
        if (customerRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "Khách hàng đã tồn tại trong hệ thống: " + dto.getPhone());
        }
        // Chuyển đổi DTO sang Entity và lưu vào cơ sở dữ liệu
        Customer saved = customerRepository.save(CustomerConverter.INSTANCE.toEntity(dto));
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

    @Override
    public CustomerVehicleResponse findByPhone(String phone) {
        Optional<Customer> result = customerRepository.findByPhone(phone);
        return result.map(CustomerConverter.INSTANCE::toCustomerVehicleResp).orElse(null);
    }

    @Override
    public CustomerVehicleResponse findCustomerVehicleByPhoneOrLicensePlate(String phoneOrLicensePlate) {
        List<CustomerVehicleFlatDto> flatList = null;
        if (phoneOrLicensePlate.matches(RegexConstant.PHONE_REGEX)) {
            flatList = customerRepository.findCustomerWithVehicleByPhone(phoneOrLicensePlate);
        }

        if (phoneOrLicensePlate.matches(RegexConstant.LICENSE_PLATE_REGEX)) {
            flatList = customerRepository.findCustomerWithVehicleByLicensePlate(phoneOrLicensePlate);
        }

        if (CollectionUtils.isNotEmpty(flatList)) {
            var first = flatList.get(0);
            List<CustomerVehicleDto> vehicles = flatList.stream()
                    .filter(item -> ObjectUtils.isNotNull(item.getLicensePlate()))
                    .map(flat -> new CustomerVehicleDto(
                            flat.getBrandCode(),
                            flat.getBrandName(),
                            flat.getModelCode(),
                            flat.getModelName(),
                            flat.getLicensePlate()))
                    .toList();
            return CustomerVehicleResponse.builder()
                    .id(first.getId())
                    .name(first.getCustomerName())
                    .phone(first.getPhone())
                    .vehicles(vehicles)
                    .build();
        }

        return null;
    }
}
