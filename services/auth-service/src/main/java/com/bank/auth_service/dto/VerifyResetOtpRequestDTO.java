package com.bank.auth_service.dto;

public record VerifyResetOtpRequestDTO(
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        String email,

        @jakarta.validation.constraints.NotBlank
        String otp
) {
}
