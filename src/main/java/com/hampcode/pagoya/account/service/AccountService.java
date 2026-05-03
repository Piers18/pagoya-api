package com.hampcode.pagoya.account.service;

import com.hampcode.pagoya.account.dto.AccountBalanceResponse;
import com.hampcode.pagoya.account.dto.AccountResponse;
import com.hampcode.pagoya.account.dto.AccountSummaryReport;
import com.hampcode.pagoya.account.dto.CreateAccountRequest;
import com.hampcode.pagoya.account.exception.DuplicateAccountTypeException;
import com.hampcode.pagoya.account.mapper.AccountMapper;
import com.hampcode.pagoya.account.model.Account;
import com.hampcode.pagoya.account.model.AccountStatus;
import com.hampcode.pagoya.account.repository.AccountRepository;
import com.hampcode.pagoya.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        // RN-07: un cliente no puede tener dos cuentas del mismo tipo
        if (accountRepository.existsByCustomerIdAndType(
                request.customerId(), request.type())) {
            throw new DuplicateAccountTypeException();
        }
        Account account = Account.builder()
            .accountNumber(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
            .balance(BigDecimal.ZERO)
            .status(AccountStatus.ACTIVE)
            .type(request.type())
            .customerId(request.customerId())
            .build();
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponse getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(accountMapper::toBalance)
            .orElseThrow(() -> new ResourceNotFoundException("cuenta no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> findByCustomer(Long customerId, Pageable pageable) {
        return accountRepository.findByCustomerId(customerId, pageable)
            .map(accountMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummaryReport> reportSummary() {
        return accountRepository.reportSummaryRaw().stream()
            .map(r -> new AccountSummaryReport(
                (String) r[0],
                (String) r[1],
                ((Number) r[2]).longValue(),
                (BigDecimal) r[3]))
            .toList();
    }
}
