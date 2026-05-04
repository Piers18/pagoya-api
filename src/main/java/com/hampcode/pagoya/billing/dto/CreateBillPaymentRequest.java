package com.hampcode.pagoya.billing.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateBillPaymentRequest(
    @NotNull Long customerId,
    @NotNull Long providerId,
    @NotBlank @Size(max = 50) String billCode,
    @NotNull
    @DecimalMin(value = "0.01", message = "el monto debe ser mayor a 0")
    @DecimalMax(value = "5000.00", message = "el monto no puede superar 5000")
    BigDecimal amount
) {}
