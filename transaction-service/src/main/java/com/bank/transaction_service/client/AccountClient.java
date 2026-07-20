package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.AccountResponse;
import com.bank.transaction_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
@Component
public class AccountClient {

    private RestClient accountRestClient;

    public AccountClient(RestClient accountRestClient) {
        this.accountRestClient = accountRestClient;
    }

    public AccountResponse getCustomerById(String accountNumber) {
        try {
            return accountRestClient.get()
                    .uri("/api/accounts/{accountNumber}", accountNumber)
                    .retrieve()
                    .body(AccountResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Account not found: " + accountNumber);
        }
    }

}
