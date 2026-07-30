package com.bank.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotNull
    private Long customerId;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
