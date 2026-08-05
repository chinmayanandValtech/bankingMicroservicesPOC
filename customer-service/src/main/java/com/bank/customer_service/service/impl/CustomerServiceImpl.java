package com.bank.customer_service.service.impl;


import com.bank.customer_service.client.KeycloakClient;
import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerAlreadyExistsException;
import com.bank.customer_service.exception.ForbiddenException;
import com.bank.customer_service.exception.ResourceNotFoundException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.security.CurrentUser;
import com.bank.customer_service.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//This class contains business logic.
//Without this annotation, Spring won't create this object automatically.
@Service
public class CustomerServiceImpl implements CustomerService {


//    This class needs Repository to talk to Database.
    private final CustomerRepository customerRepository;
    private final KeycloakClient keycloakClient;
    private final CurrentUser currentUser;

//    Spring automatically gives Repository to Service.
//    This is called
//    Dependency Injection
    public CustomerServiceImpl(CustomerRepository customerRepository,
                                KeycloakClient keycloakClient,
                                CurrentUser currentUser) {
        this.customerRepository = customerRepository;
        this.keycloakClient = keycloakClient;
        this.currentUser = currentUser;
    }

    /** Only bank staff may perform this operation. */
    private void assertAdmin(String action) {
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException("Only bank staff can " + action);
        }
    }

    /** Admins may act on any customer; a customer may only act on themselves. */
    private void assertCanAccess(Long customerId) {
        if (currentUser.isAdmin()) {
            return;
        }
        Long callerId = currentUser.getCustomerId();
        if (callerId == null || !callerId.equals(customerId)) {
            throw new ForbiddenException("You can only access your own customer record");
        }
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {

        assertAdmin("onboard new customers");

        if (customerRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException("Email already exists");
        }

        if (customerRepository.findByPhoneNumber(requestDTO.getPhoneNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Phone number already exists");
        }

        if (customerRepository.findByPanNumber(requestDTO.getPanNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("PAN number already exists");
        }
//        Create an empty Customer object.
//                Think like
//        Blank Customer Form
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

        keycloakClient.createCustomerUser(
                savedCustomer.getEmail(),
                savedCustomer.getFirstName(),
                savedCustomer.getLastName(),
                requestDTO.getPassword(),
                savedCustomer.getCustomerId()
        );

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

        // Admins see every customer; a customer sees only their own record.
        List<Customer> customers;
        if (currentUser.isAdmin()) {
            customers = customerRepository.findAll();
        } else {
            Long callerId = currentUser.getCustomerId();
            customers = callerId == null
                    ? List.of()
                    : customerRepository.findById(callerId).map(List::of).orElse(List.of());
        }

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
    public CustomerResponseDTO getCustomerById(Long customerId) {

        assertCanAccess(customerId);

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
    public CustomerResponseDTO updateCustomer(Long customerId, CustomerRequestDTO requestDTO) {

        assertCanAccess(customerId);

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
    public void deleteCustomer(Long customerId) {

        assertAdmin("delete customers");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.delete(customer);

    }
}