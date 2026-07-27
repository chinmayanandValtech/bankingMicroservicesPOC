package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "account-service")
public interface AccountFeignClient {

    @GetMapping("/api/accounts")
    List<AccountResponse> getAllAccounts();

    @PostMapping("/api/accounts/{accountNumber}/deposit")
    AccountBalanceResponse deposit(@PathVariable String accountNumber, @RequestBody DepositRequest request);


    @PostMapping("/api/accounts/{accountNumber}/withdraw")
    AccountBalanceResponse withdraw(@PathVariable String accountNumber, @RequestBody WithdrawRequest request);

    @PostMapping("/api/accounts/transfer")
    TransferResponse transfer(@RequestBody TransferRequest request);

    @GetMapping("/api/accounts/{accountNumber}")
    AccountResponse getAccountByAccountNumber(@PathVariable String accountNumber);

}
