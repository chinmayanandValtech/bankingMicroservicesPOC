package com.bank.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone Number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone Number must be exactly 10 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "PAN Number is required")
    private String panNumber;

    private String address;

    private LocalDate dateOfBirth;
}