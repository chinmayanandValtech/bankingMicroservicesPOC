package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "customer-service")
public interface CustomerFeignClient {
@GetMapping("api/customers/{customerId}")
    CustomerDto getCustomerById(Long customerId);
}
