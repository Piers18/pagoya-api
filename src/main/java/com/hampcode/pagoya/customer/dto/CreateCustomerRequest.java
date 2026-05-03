package com.hampcode.pagoya.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
    @NotBlank(message = "el nombre completo es obligatorio")
    @Size(max = 100) String fullName,

    @NotBlank(message = "el DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "el DNI debe tener 8 digitos")
    String dni,

    @Pattern(regexp = "\\d{9}", message = "el telefono debe tener 9 digitos")
    String phone,

    @NotNull(message = "el userId es obligatorio")
    Long userId
) {}
