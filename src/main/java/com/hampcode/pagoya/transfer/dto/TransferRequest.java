package com.hampcode.pagoya.transfer.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TransferRequest(
    @NotBlank(message = "la cuenta origen es obligatoria")
    String sourceAccountNumber,

    @NotBlank(message = "la cuenta destino es obligatoria")
    String targetAccountNumber,

    @NotNull(message = "el monto es obligatorio")
    @DecimalMin(value = "1.00", message = "el monto minimo es S/. 1.00")
    BigDecimal amount,

    @NotBlank(message = "la moneda es obligatoria")
    @Pattern(regexp = "PEN|USD|EUR|GBP",
             message = "la moneda debe ser PEN, USD, EUR o GBP")
    String currency
) {}
