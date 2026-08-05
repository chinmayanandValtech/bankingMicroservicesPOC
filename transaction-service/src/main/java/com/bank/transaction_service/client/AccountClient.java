package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.exception.ForbiddenException;
import com.bank.transaction_service.exception.ResourceNotFoundException;
import com.bank.transaction_service.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class AccountClient {

    private final RestClient accountRestClient;

    public AccountClient(RestClient accountRestClient) {
        this.accountRestClient = accountRestClient;
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountByAccountNumberFallback")
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
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new ForbiddenException("You are not allowed to act on account: " + accountNumber);
        }
    }

    private AccountResponse getAccountByAccountNumberFallback(String accountNumber, Throwable ex) {
        throwIfNotFound(ex);
        // fall through to the outage response below
        throw new ServiceUnavailableException("Account service is unavailable right now, please try again later");
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "depositFallback")
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
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new ForbiddenException("You are not allowed to act on account: " + accountNumber);
        }
    }

    private AccountBalanceResponse depositFallback(String accountNumber, DepositRequest request, Throwable ex) {
        throwIfNotFound(ex);
        throw new ServiceUnavailableException("Account service is unavailable right now, please try again later");
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "withdrawFallback")
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
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new ForbiddenException("You are not allowed to act on account: " + accountNumber);
        }
    }

    private AccountBalanceResponse withdrawFallback(String accountNumber, WithdrawRequest request, Throwable ex) {
        throwIfNotFound(ex);
        throw new ServiceUnavailableException("Account service is unavailable right now, please try again later");
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "transferFallback")
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
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new ForbiddenException("You are not allowed to transfer from that account");
        }
    }

    private TransferResponse transferFallback(TransferRequest request, Throwable ex) {
        throwIfNotFound(ex);
        throw new ServiceUnavailableException("Account service is unavailable right now, please try again later");
    }

    /**
     * "Not found" and "forbidden" are real answers from account-service, not
     * outages — surface them as-is instead of reporting the service as down.
     */
    private void throwIfNotFound(Throwable ex) {
        if (ex instanceof ResourceNotFoundException notFound) {
            throw notFound;
        }
        if (ex instanceof ForbiddenException forbidden) {
            throw forbidden;
        }
    }
}
