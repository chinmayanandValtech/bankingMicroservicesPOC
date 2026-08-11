package com.bank.account_service.client;

import com.bank.account_service.dto.LedgerEntryRequest;
import com.bank.account_service.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

@Component
public class TransactionLedgerClient {

    private final RestClient transactionServiceRestClient;

    public TransactionLedgerClient(RestClient transactionServiceRestClient) {
        this.transactionServiceRestClient = transactionServiceRestClient;
    }

    /**
     * Records the initial deposit made when an account is opened. Without this
     * the account would start with money that no transaction explains, and the
     * balance would never reconcile against the ledger.
     */
    public void recordOpeningDeposit(String accountNumber, BigDecimal amount) {
        LedgerEntryRequest entry = LedgerEntryRequest.builder()
                .transactionType("DEPOSIT")
                .toAccountNumber(accountNumber)
                .amount(amount)
                .remarks("Opening deposit")
                .build();

        try {
            transactionServiceRestClient.post()
                    .uri("/api/transactions/internal/ledger-entry")
                    .body(entry)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "Could not record the opening deposit, so the account was not opened. Please try again.");
        }
    }
}
