package com.hampcode.pagoya.account.service;

import com.hampcode.pagoya.account.dto.AccountBalanceResponse;
import com.hampcode.pagoya.account.dto.AccountResponse;
import com.hampcode.pagoya.account.dto.AccountSummaryReport;
import com.hampcode.pagoya.account.dto.CreateAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IAccountService {
    AccountResponse create(CreateAccountRequest request);
    AccountBalanceResponse getBalance(String accountNumber);
    Page<AccountResponse> findByCustomer(Long customerId, Pageable pageable);
    List<AccountSummaryReport> reportSummary();
}
