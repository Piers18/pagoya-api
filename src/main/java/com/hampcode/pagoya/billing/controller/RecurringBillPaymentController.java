package com.hampcode.pagoya.billing.controller;

import com.hampcode.pagoya.billing.dto.CreateRecurringPaymentRequest;
import com.hampcode.pagoya.billing.dto.RecurringPaymentResponse;
import com.hampcode.pagoya.billing.service.IRecurringBillPaymentService;
import com.hampcode.pagoya.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recurring-bill-payments")
@RequiredArgsConstructor
@Tag(name = "Recurring Bill Payments", description = "Pagos recurrentes de servicios del cliente")
public class RecurringBillPaymentController {

    private final IRecurringBillPaymentService recurringService;

    @Operation(summary = "Programar un pago recurrente")
    @PostMapping
    public ResponseEntity<RecurringPaymentResponse> schedule(
            @Valid @RequestBody CreateRecurringPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(recurringService.schedule(request));
    }

    @Operation(summary = "Listar los pagos recurrentes del cliente")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<RecurringPaymentResponse>> findByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(
            PageResponse.from(recurringService.findByCustomer(customerId, pageable)));
    }

    @Operation(summary = "Pausar un pago recurrente")
    @PatchMapping("/{id}/pause")
    public ResponseEntity<RecurringPaymentResponse> pause(@PathVariable Long id) {
        return ResponseEntity.ok(recurringService.pause(id));
    }

    @Operation(summary = "Reanudar un pago recurrente")
    @PatchMapping("/{id}/resume")
    public ResponseEntity<RecurringPaymentResponse> resume(@PathVariable Long id) {
        return ResponseEntity.ok(recurringService.resume(id));
    }

    @Operation(summary = "Cancelar un pago recurrente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        recurringService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
