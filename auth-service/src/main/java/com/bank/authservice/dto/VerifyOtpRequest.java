package com.bank.authservice.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String otp;
    private String email;
}
