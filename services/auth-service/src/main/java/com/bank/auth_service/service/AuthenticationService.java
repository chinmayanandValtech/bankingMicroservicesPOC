package com.bank.auth_service.service;

import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;
import com.bank.auth_service.dto.ApiResponse;
import com.bank.auth_service.dto.SendOtpRequest;
import com.bank.auth_service.dto.VerifyOtpRequest;

public interface AuthenticationService {
    ApiResponse login(LoginRequestDTO request);
    ApiResponse sendOtp(SendOtpRequest request);
    LoginResponseDTO verifyOtp(VerifyOtpRequest request);
}
