package com.hampcode.pagoya.customer.service;

import com.hampcode.pagoya.customer.dto.CreateCustomerRequest;
import com.hampcode.pagoya.customer.dto.CustomerResponse;
import com.hampcode.pagoya.customer.exception.CustomerProfileAlreadyExistsException;
import com.hampcode.pagoya.customer.exception.DniAlreadyExistsException;
import com.hampcode.pagoya.customer.mapper.CustomerMapper;
import com.hampcode.pagoya.customer.model.Customer;
import com.hampcode.pagoya.customer.repository.CustomerRepository;
import com.hampcode.pagoya.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        // RN-03: DNI unico
        if (customerRepository.existsByDni(request.dni())) {
            throw new DniAlreadyExistsException();
        }
        // RN-04: un usuario solo puede tener un perfil de cliente
        if (customerRepository.existsByUserId(request.userId())) {
            throw new CustomerProfileAlreadyExistsException();
        }
        Customer entity = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
            .map(customerMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("cliente no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable)
            .map(customerMapper::toResponse);
    }
}
