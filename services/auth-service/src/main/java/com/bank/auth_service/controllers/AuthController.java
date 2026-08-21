package com.bank.auth_service.controllers;

import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;
import com.bank.auth_service.dto.ApiResponse;
import com.bank.auth_service.dto.ForgotPasswordRequestDTO;
import com.bank.auth_service.dto.ResetPasswordRequestDTO;
import com.bank.auth_service.dto.SendOtpRequest;
import com.bank.auth_service.dto.VerifyOtpRequest;
import com.bank.auth_service.dto.VerifyResetOtpRequestDTO;
import com.bank.auth_service.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/api/auth/login")
    public ApiResponse login(@RequestBody LoginRequestDTO request) {
        return authenticationService.login(request);
    }

    @PostMapping("api/auth/generate-otp")
    public ApiResponse generateOtp(@RequestBody SendOtpRequest request){
        return  authenticationService.sendOtp(request);
    };

    @PostMapping("api/auth/verify-otp")
    public LoginResponseDTO verifyOtp(@RequestBody VerifyOtpRequest request){
  return authenticationService.verifyOtp(request);
    };

    @PostMapping("api/auth/forgot-password")
    public ApiResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        return authenticationService.forgotPassword(request);
    }

    @PostMapping("api/auth/verify-reset-otp")
    public ApiResponse verifyResetOtp(@Valid @RequestBody VerifyResetOtpRequestDTO request) {
        return authenticationService.verifyResetOtp(request);
    }

    @PostMapping("api/auth/reset-password")
    public ApiResponse resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        return authenticationService.resetPassword(request);
    }
}
