package service;

import com.bank.customer_service.dto.CustomerRequestDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerAlreadyExistsException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @Test
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .build();

        Customer savedCustomer = Customer.builder()
                .customerId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(customerRepository.findByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(Optional.empty());

        when(customerRepository.findByPanNumber(request.getPanNumber()))
                .thenReturn(Optional.empty());

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        // Act
        CustomerResponseDTO response =
                customerService.createCustomer(request);


        // Assert

        assertNotNull(response);

        assertEquals(1L, response.getCustomerId());
        assertEquals("John", response.getFirstName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("9876543210", response.getPhoneNumber());

        verify(customerRepository).findByEmail(request.getEmail());
        verify(customerRepository).findByPhoneNumber(request.getPhoneNumber());
        verify(customerRepository).findByPanNumber(request.getPanNumber());
        verify(customerRepository).save(any(Customer.class));

    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .address("Bangalore")
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .build();

        Customer existingCustomer = Customer.builder()
                .customerId(100L)
                .email("john@example.com")
                .build();

        when(customerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingCustomer));

        CustomerAlreadyExistsException exception =
                assertThrows(
                        CustomerAlreadyExistsException.class,
                        () -> customerService.createCustomer(request)
                );

        assertEquals("Email already exists", exception.getMessage());

        verify(customerRepository).findByEmail(request.getEmail());

        verify(customerRepository, never())
                .findByPhoneNumber(anyString());

        verify(customerRepository, never())
                .findByPanNumber(anyString());

        verify(customerRepository, never())
                .save(any(Customer.class));
    }
}
