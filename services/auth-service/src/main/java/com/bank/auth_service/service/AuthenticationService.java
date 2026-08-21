package com.bank.auth_service.service;

import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;
import com.bank.auth_service.dto.*;

public interface AuthenticationService {
    ApiResponse login(LoginRequestDTO request);
    ApiResponse sendOtp(SendOtpRequest request);
    LoginResponseDTO verifyOtp(VerifyOtpRequest request);
    ApiResponse forgotPassword(ForgotPasswordRequestDTO request);
    ApiResponse verifyResetOtp(VerifyResetOtpRequestDTO request);
    ApiResponse resetPassword(ResetPasswordRequestDTO request);
}
