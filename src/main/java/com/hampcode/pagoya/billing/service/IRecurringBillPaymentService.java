package com.hampcode.pagoya.billing.service;

import com.hampcode.pagoya.billing.dto.CreateRecurringPaymentRequest;
import com.hampcode.pagoya.billing.dto.RecurringPaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRecurringBillPaymentService {
    RecurringPaymentResponse schedule(CreateRecurringPaymentRequest request);
    Page<RecurringPaymentResponse> findByCustomer(Long customerId, Pageable pageable);
    RecurringPaymentResponse pause(Long id);
    RecurringPaymentResponse resume(Long id);
    void cancel(Long id);
    void processDuePayments(); // For the scheduler
}
