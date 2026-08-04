package com.bank.customer_service.controller;


import com.bank.customer_service.dto.CustomerLookupRequestDTO;
import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;

import com.bank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO requestDTO) {

        CustomerResponseDTO response =
                customerService.createCustomer(requestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {

        List<CustomerResponseDTO> customers =
                customerService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(
            @PathVariable Long customerId) {

        CustomerResponseDTO response =
                customerService.getCustomerById(customerId);

        return ResponseEntity.ok(response);
    }

    /**
     * Unauthenticated service-to-service lookup, used by auth-service during
     * registration (before a user has any JWT). Not exposed externally -
     * blocked at the API Gateway.
     */
    @PostMapping("/internal")
    public ResponseEntity<CustomerResponseDTO> getCustomerByIdInternal(
            @Valid @RequestBody CustomerLookupRequestDTO request) {

        CustomerResponseDTO response =
                customerService.getCustomerById(request.getCustomerId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/internal/email/{email}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(
            @PathVariable String email) {

        CustomerResponseDTO response =
                customerService.getCustomerByEmail(email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {

        CustomerResponseDTO response =
                customerService.updateCustomer(customerId, requestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long customerId) {

        customerService.deleteCustomer(customerId);

        return ResponseEntity.ok("Customer deleted successfully");
    }
}