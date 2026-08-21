package com.bank.auth_service.dto;

public record ForgotPasswordRequestDTO(
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        String email
) {
}
