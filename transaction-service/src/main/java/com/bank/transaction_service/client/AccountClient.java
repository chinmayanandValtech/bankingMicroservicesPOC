package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
@Component
public class AccountClient {

    private final RestClient accountRestClient;

    public AccountClient(RestClient.Builder builder) {
        this.accountRestClient = builder
                .baseUrl("http://account-service")
                .build();
    }

    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        try {
            return accountRestClient.get()
                    .uri("/api/accounts/{accountNumber}", accountNumber)
                    .retrieve()
                    .body(AccountResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Account not found: " + accountNumber
            );
        }
    }

    public AccountBalanceResponse deposit(String accountNumber,
                                          DepositRequest request) {
        try {
            return accountRestClient.post()
                    .uri("/api/accounts/{accountNumber}/deposit", accountNumber)
                    .body(request)
                    .retrieve()
                    .body(AccountBalanceResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Account not found: " + accountNumber
            );
        }
    }

    public AccountBalanceResponse withdraw(String accountNumber,
                                           WithdrawRequest request) {
        try {
            return accountRestClient.post()
                    .uri("/api/accounts/{accountNumber}/withdraw", accountNumber)
                    .body(request)
                    .retrieve()
                    .body(AccountBalanceResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Account not found: " + accountNumber
            );
        }
    }

    public TransferResponse transfer(TransferRequest request) {
        try {
            return accountRestClient.post()
                    .uri("/api/accounts/transfer")
                    .body(request)
                    .retrieve()
                    .body(TransferResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "One or both accounts were not found."
            );
        }
    }
}
