package com.alphawash.converter;

import com.alphawash.dto.CustomerDto;
import com.alphawash.entity.Customer;
import com.alphawash.response.CustomerResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerConverter {
    CustomerConverter INSTANCE = Mappers.getMapper(CustomerConverter.class);

    CustomerResponse toResponse(CustomerDto dto);

    CustomerDto toDto(Customer entity);

    Customer toEntity(CustomerDto dto);

    List<CustomerDto> toDto(List<Customer> entities);

    List<CustomerResponse> toResponse(List<CustomerDto> dtos);

    //    @Mapping(target = "id", source = "id")
    //    CustomerVehicleResponse toCustomerVehicleResp(Customer customer);
}
