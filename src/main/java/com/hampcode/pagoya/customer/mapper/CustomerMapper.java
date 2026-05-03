package com.hampcode.pagoya.customer.mapper;

import com.hampcode.pagoya.customer.dto.CreateCustomerRequest;
import com.hampcode.pagoya.customer.dto.CustomerResponse;
import com.hampcode.pagoya.customer.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    Customer toEntity(CreateCustomerRequest request);
    CustomerResponse toResponse(Customer customer);
}
