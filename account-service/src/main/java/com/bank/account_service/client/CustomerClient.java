package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerDto;
import com.bank.account_service.exception.ResourceNotFoundException;
import com.bank.account_service.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class CustomerClient {

    private final RestClient customerServiceRestClient;

    public CustomerClient(RestClient customerServiceRestClient) {
        this.customerServiceRestClient = customerServiceRestClient;
    }

    @Retry(name = "customerService")
    @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerByIdFallback")
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

    private CustomerDto getCustomerByIdFallback(Long customerId, Throwable ex) {
        if (ex instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) ex;
        }
        throw new ServiceUnavailableException("Customer service is unavailable right now, please try again later");
    }
}
