package com.hampcode.pagoya.account.controller;

import com.hampcode.pagoya.account.dto.AccountBalanceResponse;
import com.hampcode.pagoya.account.dto.AccountResponse;
import com.hampcode.pagoya.account.dto.CreateAccountRequest;
import com.hampcode.pagoya.account.service.IAccountService;
import com.hampcode.pagoya.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Cuentas de los clientes")
public class AccountController {

    private final IAccountService accountService;

    @Operation(summary = "Crear una cuenta para el cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cuenta creada"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o cuenta duplicada")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(accountService.create(request));
    }

    @Operation(summary = "Consultar el saldo de una cuenta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo entregado"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountBalanceResponse> getBalance(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @Operation(summary = "Listar cuentas de un cliente (paginado)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista paginada de cuentas")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<AccountResponse>> findByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(
            PageResponse.from(accountService.findByCustomer(customerId, pageable)));
    }
}
