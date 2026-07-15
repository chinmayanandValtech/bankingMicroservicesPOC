package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerDto;
import com.bank.account_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class CustomerClient {

    private final RestClient customerServiceRestClient;

    public CustomerClient(RestClient customerServiceRestClient) {
        this.customerServiceRestClient = customerServiceRestClient;
    }

    public CustomerDto getCustomerById(Long customerId) {
        try {
            return customerServiceRestClient.get()
                    .uri("/api/customers/{customerId}", customerId)
                    .retrieve()
                    .body(CustomerDto.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
    }
}
