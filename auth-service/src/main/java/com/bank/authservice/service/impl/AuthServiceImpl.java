package com.bank.authservice.service.impl;

import com.bank.authservice.dto.AuthResponse;
import com.bank.authservice.dto.LoginRequest;
import com.bank.authservice.dto.RegisterRequest;
import com.bank.authservice.dto.VerifyOtpRequest;
import com.bank.authservice.entity.User;
import com.bank.authservice.repository.UserRepository;
import com.bank.authservice.service.AuthService;
import com.bank.authservice.service.EmailService;
import com.bank.authservice.service.JwtService;
import com.bank.authservice.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;

    @Override
    public String register(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role("USER")
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid password");
        }

        String otp = otpService.generateOtp();

        otpService.saveOtp(user.getEmail(), otp);

        System.out.println("Generated OTP = " + otp);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return AuthResponse.builder()
                .token("OTP sent successfully")
                .build();
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {

        System.out.println("Verify OTP API called");

        boolean valid = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        System.out.println("OTP valid = " + valid);

        if (!valid) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtService.generateToken(
                user.getUsername()
        );

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public String resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"));
        String otp = otpService.generateOtp();
        otpService.saveOtp(email,otp);
        emailService.sendOtpEmail(email,otp);
        return "OTP resent successfully";
    }

}