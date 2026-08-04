package com.bank.auth_service.client;

import com.bank.auth_service.dto.CustomerLookupRequestDTO;
import com.bank.auth_service.dto.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    // Unauthenticated internal lookup: registration happens before any JWT exists.
    @PostMapping("/api/customers/internal")
    CustomerResponseDTO getCustomerById(@RequestBody CustomerLookupRequestDTO request);

    @GetMapping("/api/customers/internal/email/{email}")
    Optional<CustomerResponseDTO> getCustomerByEmail(@PathVariable String email);
}
