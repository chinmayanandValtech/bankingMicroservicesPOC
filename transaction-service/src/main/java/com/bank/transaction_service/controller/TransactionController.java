package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

   @PostMapping("/deposit")
    private TransactionResponse moneyDeposit(@Valid @RequestBody DepositRequest request){
       return  transactionService.deposit(request);

   }
    @PostMapping("/withdraw")
    private TransactionResponse moneyWithDraw(@Valid @RequestBody WithdrawRequest request){
        return  transactionService.withdraw(request);

    }

    @PostMapping("/transfer")
    private TransactionResponse moneyTransfer(@Valid @RequestBody TransferRequest request){
        return  transactionService.transfer(request);

    }

    /**
     * Internal only — guarded by InternalCallFilter. Lets account-service record
     * the opening deposit, which it applies itself at account creation.
     */
    @PostMapping("/internal/ledger-entry")
    public ResponseEntity<TransactionResponse> recordLedgerEntry(
            @Valid @RequestBody LedgerEntryRequest request) {
        return ResponseEntity.ok(transactionService.recordLedgerEntry(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                transactionService.getTransactionHistory(accountNumber)
        );
    }
}
