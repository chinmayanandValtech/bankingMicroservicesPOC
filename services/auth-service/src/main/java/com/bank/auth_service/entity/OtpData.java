package com.bank.auth_service.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpData {

    private String otp;

    private int attempts;

    private LocalDateTime generatedAt;

}
