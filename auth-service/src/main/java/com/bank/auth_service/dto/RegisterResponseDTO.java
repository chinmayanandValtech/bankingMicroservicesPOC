package com.bank.auth_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {

    private Long userId;
    private Long customerId;
    private String email;
    private String message;
}
