package com.bank.authservice.service.impl;

import com.bank.authservice.dto.AuthResponse;
import com.bank.authservice.dto.LoginRequest;
import com.bank.authservice.dto.RegisterRequest;
import com.bank.authservice.dto.VerifyOtpRequest;
import com.bank.authservice.entity.User;
import com.bank.authservice.repository.UserRepository;
import com.bank.authservice.service.AuthService;
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

    @Override
    public String register(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role("USER")
                .build();

        userRepository.save(user);

        return "User Registered Successfully";
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

        System.out.println("OTP = " + otp);

        return AuthResponse.builder()
                .token("OTP sent successfully")
                .build();
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {

        boolean valid = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

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
}



//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTaHJleWEiLCJpYXQiOjE3ODQ4NzIwMzIsImV4cCI6MTc4NDg3NTYzMn0.jaonFqJQZ5MefE1rrTPRFn3XH_Nt3k9OVrh9jpaTS-k