package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerAlreadyExistsException;
import com.bank.customer_service.exception.ResourceNotFoundException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {

        if (customerRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException("Email already exists");
        }

        if (customerRepository.findByPhoneNumber(requestDTO.getPhoneNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Phone number already exists");
        }

        if (customerRepository.findByPanNumber(requestDTO.getPanNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("PAN number already exists");
        }

        Customer customer = Customer.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .email(requestDTO.getEmail())
                .phoneNumber(requestDTO.getPhoneNumber())
                .panNumber(requestDTO.getPanNumber())
                .address(requestDTO.getAddress())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .createdAt(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerResponseDTO.builder()
                .customerId(savedCustomer.getCustomerId())
                .firstName(savedCustomer.getFirstName())
                .lastName(savedCustomer.getLastName())
                .email(savedCustomer.getEmail())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .address(savedCustomer.getAddress())
                .dateOfBirth(savedCustomer.getDateOfBirth())
                .createdAt(savedCustomer.getCreatedAt())
                .build();
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {

        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .map(customer -> CustomerResponseDTO.builder()
                        .customerId(customer.getCustomerId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .email(customer.getEmail())
                        .phoneNumber(customer.getPhoneNumber())
                        .address(customer.getAddress())
                        .dateOfBirth(customer.getDateOfBirth())
                        .createdAt(customer.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Cacheable(value = "customers", key = "#customerId")
    public CustomerResponseDTO getCustomerById(Long customerId) {

        System.out.println("Fetching customer from PostgreSQL...");

        Optional<Customer> optionalCustomer =
                customerRepository.findById(customerId);

        Customer customer = optionalCustomer.orElseThrow(
                () -> new ResourceNotFoundException("Customer not found")
        );

        return CustomerResponseDTO.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .dateOfBirth(customer.getDateOfBirth())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    @Override
    @CachePut(value = "customers", key = "#customerId")
    public CustomerResponseDTO updateCustomer(Long customerId, CustomerRequestDTO requestDTO) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setFirstName(requestDTO.getFirstName());
        customer.setLastName(requestDTO.getLastName());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhoneNumber(requestDTO.getPhoneNumber());
        customer.setPanNumber(requestDTO.getPanNumber());
        customer.setAddress(requestDTO.getAddress());
        customer.setDateOfBirth(requestDTO.getDateOfBirth());

        Customer updatedCustomer = customerRepository.save(customer);

        return CustomerResponseDTO.builder()
                .customerId(updatedCustomer.getCustomerId())
                .firstName(updatedCustomer.getFirstName())
                .lastName(updatedCustomer.getLastName())
                .email(updatedCustomer.getEmail())
                .phoneNumber(updatedCustomer.getPhoneNumber())
                .address(updatedCustomer.getAddress())
                .dateOfBirth(updatedCustomer.getDateOfBirth())
                .createdAt(updatedCustomer.getCreatedAt())
                .build();
    }

    @Override
    @CacheEvict(value = "customers", key = "#customerId")
    public void deleteCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.delete(customer);
    }
}