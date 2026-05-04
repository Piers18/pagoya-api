package com.hampcode.pagoya.billing.dto;

import com.hampcode.pagoya.billing.model.RecurringFrequency;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateRecurringPaymentRequest(
    @NotNull Long customerId,
    @NotNull Long providerId,
    @NotBlank @Size(max = 50) String billCode,
    @NotNull
    @DecimalMin(value = "0.01", message = "el monto debe ser mayor a 0")
    @DecimalMax(value = "5000.00", message = "el monto no puede superar 5000")
    BigDecimal amount,
    @NotNull RecurringFrequency frequency,
    @Min(1) @Max(28) Integer dayOfMonth,
    @Min(1) @Max(7) Integer dayOfWeek
) {}
