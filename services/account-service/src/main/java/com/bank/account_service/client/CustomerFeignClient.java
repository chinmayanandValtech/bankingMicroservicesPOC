package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerFeignClient {
    @GetMapping("api/customers/{customerId}")
    CustomerDto getCustomerById(@PathVariable Long customerId);

    @GetMapping("/api/customers/internalemail/{email}")
    CustomerDto getCustomerByEmail(@PathVariable String email);
}
