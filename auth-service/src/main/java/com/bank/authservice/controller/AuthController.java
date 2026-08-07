package com.bank.authservice.controller;

import com.bank.authservice.dto.AuthResponse;
import com.bank.authservice.dto.LoginRequest;
import com.bank.authservice.dto.RegisterRequest;
import com.bank.authservice.dto.VerifyOtpRequest;
import com.bank.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public String resendOtp(
            @RequestParam String email) {

        return authService.resendOtp(email);
    }
}