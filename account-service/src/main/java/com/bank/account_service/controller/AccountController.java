package com.bank.account_service.controller;

import com.bank.account_service.dto.AccountBalanceResponse;
import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.DepositRequest;
import com.bank.account_service.dto.TransferRequest;
import com.bank.account_service.dto.TransferResponse;
import com.bank.account_service.dto.WithdrawRequest;
import com.bank.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse response = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountById(accountNumber));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
                                                          @Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountBalanceResponse> deposit(@PathVariable String accountNumber,
                                                            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(accountService.deposit(accountNumber, request));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountBalanceResponse> withdraw(@PathVariable String accountNumber,
                                                             @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(accountService.withdraw(accountNumber, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(request));
    }
}
