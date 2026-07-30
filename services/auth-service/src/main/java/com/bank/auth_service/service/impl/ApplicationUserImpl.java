package com.bank.auth_service.service.impl;


import com.bank.auth_service.client.CustomerClient;
import com.bank.auth_service.config.SecurityConfig;
import com.bank.auth_service.dto.CustomerLookupRequestDTO;
import com.bank.auth_service.dto.CustomerResponseDTO;
import com.bank.auth_service.dto.RegisterRequestDTO;
import com.bank.auth_service.dto.RegisterResponseDTO;
import com.bank.auth_service.entity.ApplicationUser;
import com.bank.auth_service.enums.Role;
import com.bank.auth_service.exceptions.CustomErrorException;
import com.bank.auth_service.repository.ApplicationUserRepository;
import com.bank.auth_service.service.ApplicationUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApplicationUserImpl implements ApplicationUserService {

    private final CustomerClient customerClient;
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    public ApplicationUserImpl(CustomerClient customerClient, ApplicationUserRepository applicationUserRepository, PasswordEncoder passwordEncoder) {
        this.customerClient = customerClient;
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
        // Fetch customer details from Customer Service
        CustomerResponseDTO customerResponse =
                customerClient.getCustomerById(
                        CustomerLookupRequestDTO.builder()
                                .customerId(request.getCustomerId())
                                .build());


        // Check if this customer is already registered
        applicationUserRepository.findByCustomerId(customerResponse.getCustomerId())
                .ifPresent(user -> {
                    throw new CustomErrorException(
                            "Customer is already registered.");
                });

        // Check if email is already registered
        applicationUserRepository.findByEmail(customerResponse.getEmail())
                .ifPresent(user -> {
                    throw new CustomErrorException(
                            "Email is already registered.");
                });

        // Create ApplicationUser
        ApplicationUser applicationUser = ApplicationUser.builder()
                .customerId(customerResponse.getCustomerId())
                .email(customerResponse.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt in next step
                .role(Role.CUSTOMER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // Save user
        ApplicationUser savedUser = applicationUserRepository.save(applicationUser);

        // Build response
        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .customerId(savedUser.getCustomerId())
                .email(savedUser.getEmail())
                .message("User registered successfully")
                .build();
    }
}
