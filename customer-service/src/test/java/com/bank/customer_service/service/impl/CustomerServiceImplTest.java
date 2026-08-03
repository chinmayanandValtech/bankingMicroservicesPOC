package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerAlreadyExistsException;
import com.bank.customer_service.exception.ResourceNotFoundException;
import com.bank.customer_service.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCustomerWhenCustomerExists() {

        Customer customer = Customer.builder()
                .customerId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .phoneNumber("9999999999")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponseDTO response =
                customerService.getCustomerById(1L);

        assertEquals("John", response.getFirstName());

        verify(customerRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(100L)
        );

        verify(customerRepository).findById(100L);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .build();

        Customer existingCustomer = Customer.builder()
                .customerId(1L)
                .firstName("John")
                .email("john@gmail.com")
                .build();

        when(customerRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(existingCustomer));

        assertThrows(

                CustomerAlreadyExistsException.class,

                () -> customerService.createCustomer(requestDTO)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {

        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@gmail.com")
                .phoneNumber("8888888888")
                .panNumber("ABCDE1234F")
                .address("Mumbai")
                .build();

        Customer existingCustomer = Customer.builder()
                .customerId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .phoneNumber("9999999999")
                .panNumber("XYZ123")
                .address("Bangalore")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(existingCustomer));

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(existingCustomer);

        CustomerResponseDTO response =
                customerService.updateCustomer(1L, requestDTO);

        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("jane@gmail.com", response.getEmail());

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldCreateCustomerSuccessfully() {

        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .build();

        Customer savedCustomer = Customer.builder()
                .customerId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(customerRepository.findByPhoneNumber(requestDTO.getPhoneNumber()))
                .thenReturn(Optional.empty());

        when(customerRepository.findByPanNumber(requestDTO.getPanNumber()))
                .thenReturn(Optional.empty());

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponseDTO response =
                customerService.createCustomer(requestDTO);

        assertEquals(1L, response.getCustomerId());
        assertEquals("John", response.getFirstName());
        assertEquals("john@gmail.com", response.getEmail());

        verify(customerRepository).save(any(Customer.class));
    }
}