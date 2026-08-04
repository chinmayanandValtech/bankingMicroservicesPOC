package com.bank.auth_service.service.impl;

import com.bank.auth_service.client.CustomerClient;
import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;
import com.bank.auth_service.dto.ApiResponse;
import com.bank.auth_service.dto.CustomerResponseDTO;
import com.bank.auth_service.dto.SendOtpRequest;
import com.bank.auth_service.dto.VerifyOtpRequest;
import com.bank.auth_service.entity.OtpData;
import com.bank.auth_service.exceptions.CustomErrorException;
import com.bank.auth_service.security.JwtService;
import com.bank.auth_service.service.AuthenticationService;
import com.bank.auth_service.service.EmailService;
import com.bank.auth_service.utility.OtpGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerClient customerClient;
    private final RedisTemplate<String, OtpData> redisTemplate;
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, CustomerClient customerClient, RedisTemplate<String, OtpData> redisTemplate, OtpGenerator otpGenerator, EmailService emailService, UserDetailsService userDetailsService, UserDetailsService userDetailsService1) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customerClient = customerClient;
        this.redisTemplate = redisTemplate;
        this.otpGenerator = otpGenerator;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService1;
    }

    @Override
    public ApiResponse login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        sendOtp(
                new SendOtpRequest(request.getEmail())
        );

        return ApiResponse.builder()
                .message("OTP sent successfully to " +" "+ request.getEmail())
                .build();
    }

    @Override
    public ApiResponse sendOtp(SendOtpRequest request) {
    CustomerResponseDTO customer =customerClient.getCustomerByEmail(request.getEmail()).orElseThrow(() -> new CustomErrorException("Customer not found"));

        String otp = otpGenerator.generateOtp();
        OtpData otpData = new OtpData();

        otpData.setOtp(otp);
        otpData.setAttempts(0);
        otpData.setGeneratedAt(LocalDateTime.now());

        String key = "otp:" + customer.getCustomerId();
        redisTemplate.opsForValue().set(
                key,
                otpData,
                Duration.ofMinutes(5)
        );
        System.out.println(
                "OTP for " + customer.getEmail() + " : " + otp
        );
        emailService.sendOtp(customer.getEmail(), otp);
        return ApiResponse.builder()
                .message("OTP sent successfully to " +" "+ customer.getEmail())
                .build();

    }

    @Override
    public LoginResponseDTO verifyOtp(VerifyOtpRequest request) {
        CustomerResponseDTO customer = customerClient.getCustomerByEmail(request.getEmail()).orElseThrow(() -> new CustomErrorException("Customer not found"));
        String key = "otp:" + customer.getCustomerId();

        OtpData otpData = redisTemplate.opsForValue().get(key);

        if (otpData == null) {
            throw new CustomErrorException("OTP expired");
        }

       boolean isOtpValid = otpData.getOtp().equals(request.getOtp());

        if(!isOtpValid){
            throw new CustomErrorException("Invalid OTP");
        }
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(userDetails);

        redisTemplate.delete(key);

        return LoginResponseDTO.builder()
                .token(token)
                .message("Login Successful")
                .build();

    }
}
