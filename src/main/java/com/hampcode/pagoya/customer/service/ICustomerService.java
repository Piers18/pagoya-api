package com.hampcode.pagoya.customer.service;

import com.hampcode.pagoya.customer.dto.CreateCustomerRequest;
import com.hampcode.pagoya.customer.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICustomerService {
    CustomerResponse create(CreateCustomerRequest request);
    CustomerResponse findById(Long id);
    Page<CustomerResponse> findAll(Pageable pageable);
}
