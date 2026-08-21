package com.bank.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String otp,

        @NotBlank
        @Size(min = 8)
        String newPassword
) {
}