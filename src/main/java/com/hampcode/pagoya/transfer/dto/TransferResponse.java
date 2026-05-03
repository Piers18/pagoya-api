package com.hampcode.pagoya.transfer.dto;

import com.hampcode.pagoya.transfer.model.TransferStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
    Long id,
    String sourceAccountNumber,
    String targetAccountNumber,
    BigDecimal amount,
    String currency,
    BigDecimal exchangeRate,
    TransferStatus status,
    LocalDateTime createdAt
) {}
