package com.bank.customer_service.service;



import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);

    // inorder to get all customer details we use List<>
    //GET ALL CUSTOMER
    List<CustomerResponseDTO> getAllCustomers();

    // FIND CUSTOMER BY ID
    CustomerResponseDTO getCustomerById(Long customerId);

    CustomerResponseDTO getCustomerByEmail(String email);

    //UPDATE CUSTOMER
    CustomerResponseDTO updateCustomer(Long customerId, CustomerRequestDTO requestDTO);

    //DELETE
    void deleteCustomer(Long customerId);
}