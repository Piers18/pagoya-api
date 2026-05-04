package com.hampcode.pagoya.billing.service;

import com.hampcode.pagoya.billing.dto.CreateRecurringPaymentRequest;
import com.hampcode.pagoya.billing.dto.RecurringPaymentResponse;
import com.hampcode.pagoya.billing.exception.InactiveProviderException;
import com.hampcode.pagoya.billing.exception.InvalidStatusTransitionException;
import com.hampcode.pagoya.billing.mapper.RecurringPaymentMapper;
import com.hampcode.pagoya.billing.model.*;
import com.hampcode.pagoya.billing.repository.RecurringBillPaymentRepository;
import com.hampcode.pagoya.billing.repository.ServiceProviderRepository;
import com.hampcode.pagoya.billing.repository.BillPaymentRepository;
import com.hampcode.pagoya.customer.model.Customer;
import com.hampcode.pagoya.customer.repository.CustomerRepository;
import com.hampcode.pagoya.shared.exception.BusinessRuleException;
import com.hampcode.pagoya.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurringBillPaymentService implements IRecurringBillPaymentService {

    private final RecurringBillPaymentRepository recurringRepository;
    private final ServiceProviderRepository providerRepository;
    private final CustomerRepository customerRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final RecurringPaymentMapper mapper;

    @Override
    @Transactional
    public RecurringPaymentResponse schedule(CreateRecurringPaymentRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
            .orElseThrow(() -> new ResourceNotFoundException("cliente no encontrado"));

        ServiceProvider provider = providerRepository.findById(request.providerId())
            .orElseThrow(() -> new ResourceNotFoundException("proveedor no encontrado"));

        if (!provider.isActive()) {
            throw new InactiveProviderException();
        }

        if (request.frequency() == RecurringFrequency.MONTHLY) {
            if (request.dayOfMonth() == null || request.dayOfMonth() < 1 || request.dayOfMonth() > 28) {
                throw new BusinessRuleException("Frecuencia mensual requiere dia del mes entre 1 y 28");
            }
        } else if (request.frequency() == RecurringFrequency.WEEKLY) {
            if (request.dayOfWeek() == null || request.dayOfWeek() < 1 || request.dayOfWeek() > 7) {
                throw new BusinessRuleException("Frecuencia semanal requiere dia de la semana entre 1 y 7");
            }
        }

        RecurringBillPayment entity = mapper.toEntity(request);
        entity.setCustomer(customer);
        entity.setProvider(provider);
        entity.setStatus(RecurringStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setNextRunAt(calculateNextRunAt(entity, LocalDateTime.now()));

        return mapper.toResponse(recurringRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecurringPaymentResponse> findByCustomer(Long customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("cliente no encontrado");
        }
        return recurringRepository.findByCustomer_Id(customerId, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public RecurringPaymentResponse pause(Long id) {
        RecurringBillPayment payment = getById(id);
        
        if (payment.getStatus() == RecurringStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("No se puede pausar un pago cancelado");
        }
        if (payment.getStatus() != RecurringStatus.ACTIVE) {
            throw new InvalidStatusTransitionException("Solo se puede pausar un pago activo");
        }
        
        payment.setStatus(RecurringStatus.PAUSED);
        payment.setNextRunAt(null);
        return mapper.toResponse(recurringRepository.save(payment));
    }

    @Override
    @Transactional
    public RecurringPaymentResponse resume(Long id) {
        RecurringBillPayment payment = getById(id);
        
        if (payment.getStatus() == RecurringStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("No se puede reanudar un pago cancelado");
        }
        if (payment.getStatus() != RecurringStatus.PAUSED) {
            throw new InvalidStatusTransitionException("Solo se puede reanudar un pago pausado");
        }
        
        payment.setStatus(RecurringStatus.ACTIVE);
        payment.setNextRunAt(calculateNextRunAt(payment, LocalDateTime.now()));
        return mapper.toResponse(recurringRepository.save(payment));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        RecurringBillPayment payment = getById(id);
        
        if (payment.getStatus() == RecurringStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("El pago ya esta cancelado");
        }
        
        payment.setStatus(RecurringStatus.CANCELLED);
        payment.setNextRunAt(null);
        recurringRepository.save(payment);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Override
    @Transactional
    public void processDuePayments() {
        LocalDateTime now = LocalDateTime.now();
        List<RecurringBillPayment> duePayments = recurringRepository
            .findByStatusAndNextRunAtBefore(RecurringStatus.ACTIVE, now);

        for (RecurringBillPayment recurring : duePayments) {
            BillPayment billPayment = BillPayment.builder()
                .customer(recurring.getCustomer())
                .provider(recurring.getProvider())
                .billCode(recurring.getBillCode() + "-" + now.getYear() + "-" + now.getMonthValue())
                .amount(recurring.getAmount())
                .status(PaymentStatus.PAID)
                .paidAt(now)
                .createdAt(now)
                .build();
            
            billPaymentRepository.save(billPayment);
            
            recurring.setNextRunAt(calculateNextRunAt(recurring, now));
            recurringRepository.save(recurring);
        }
    }

    private RecurringBillPayment getById(Long id) {
        return recurringRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pago recurrente no encontrado"));
    }

    private LocalDateTime calculateNextRunAt(RecurringBillPayment payment, LocalDateTime from) {
        LocalDate fromDate = from.toLocalDate();
        LocalDate nextDate = fromDate;
        
        if (payment.getFrequency() == RecurringFrequency.MONTHLY) {
            if (fromDate.getDayOfMonth() >= payment.getDayOfMonth()) {
                nextDate = fromDate.plusMonths(1).withDayOfMonth(payment.getDayOfMonth());
            } else {
                nextDate = fromDate.withDayOfMonth(payment.getDayOfMonth());
            }
        } else if (payment.getFrequency() == RecurringFrequency.WEEKLY) {
            DayOfWeek targetDay = DayOfWeek.of(payment.getDayOfWeek());
            int daysToAdd = targetDay.getValue() - fromDate.getDayOfWeek().getValue();
            if (daysToAdd <= 0) {
                daysToAdd += 7;
            }
            nextDate = fromDate.plusDays(daysToAdd);
        }
        
        return nextDate.atTime(LocalTime.of(8, 0)); // Default run time at 8:00 AM
    }
}
