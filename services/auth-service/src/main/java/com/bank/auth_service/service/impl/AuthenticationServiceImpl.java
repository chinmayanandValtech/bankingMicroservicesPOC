package com.bank.auth_service.service.impl;

import com.bank.auth_service.client.CustomerClient;
import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;
import com.bank.auth_service.dto.*;
import com.bank.auth_service.entity.ApplicationUser;
import com.bank.auth_service.entity.OtpData;
import com.bank.auth_service.enums.OtpPurpose;
import com.bank.auth_service.exceptions.CustomErrorException;
import com.bank.auth_service.security.JwtService;
import com.bank.auth_service.service.ApplicationUserService;
import com.bank.auth_service.service.AuthenticationService;
import com.bank.auth_service.service.EmailService;
import com.bank.auth_service.utility.OtpGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerClient customerClient;
    private final RedisTemplate<String, OtpData> redisTemplate;
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, CustomerClient customerClient, RedisTemplate<String, OtpData> redisTemplate, OtpGenerator otpGenerator, EmailService emailService, UserDetailsService userDetailsService, UserDetailsService userDetailsService1, PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customerClient = customerClient;
        this.redisTemplate = redisTemplate;
        this.otpGenerator = otpGenerator;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService1;
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }

    @Override
    public ApiResponse login(LoginRequestDTO request) {

        try {

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
                    .message("OTP sent successfully to " + request.getEmail())
                    .build();

        } catch (BadCredentialsException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }
    }

    @Override
    public ApiResponse sendOtp(SendOtpRequest request) {
        CustomerResponseDTO customer = customerClient.getCustomerByEmail(request.getEmail())
                .orElseThrow(() -> new CustomErrorException("Customer not found"));

        generateAndSendOtp(customer, OtpPurpose.LOGIN);

        return ApiResponse.builder()
                .message("OTP sent successfully to " + " " + customer.getEmail())
                .build();
    }

    @Override
    public LoginResponseDTO verifyOtp(VerifyOtpRequest request) {
        CustomerResponseDTO customer = customerClient.getCustomerByEmail(request.getEmail())
                .orElseThrow(() -> new CustomErrorException("Customer not found"));

        validateOtp(customer, request.getOtp(), OtpPurpose.LOGIN, true);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(userDetails);

        return LoginResponseDTO.builder()
                .token(token)
                .message("Login Successful")
                .build();
    }

    @Override
    public ApiResponse verifyResetOtp(
            VerifyResetOtpRequestDTO request
    ) {

        CustomerResponseDTO customer = customerClient.getCustomerByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid or expired OTP"
                ));

        // Only checks the OTP; does not consume it, so the caller can still
        // submit the new password afterwards via resetPassword().
        validateOtp(customer, request.otp(), OtpPurpose.PASSWORD_RESET, false);

        return ApiResponse.builder()
                .message("OTP verified successfully")
                .build();
    }

    @Override
    public ApiResponse forgotPassword(
            ForgotPasswordRequestDTO request
    ) {

        customerClient.getCustomerByEmail(request.email())
                .ifPresent(customer -> generateAndSendOtp(customer, OtpPurpose.PASSWORD_RESET));

        return ApiResponse.builder()
                .message(
                        "If an account exists for this email, " +
                                "a password reset OTP has been sent."
                )
                .build();
    }

    @Override
    public ApiResponse resetPassword(
            ResetPasswordRequestDTO request
    ) {

        CustomerResponseDTO customer =
                customerClient.getCustomerByEmail(request.email())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid password reset request"
                                )
                        );

        // Consumed here so the OTP can't be replayed for another reset.
        validateOtp(customer, request.otp(), OtpPurpose.PASSWORD_RESET, true);

        applicationUserService.updateUser(
                RegisterRequestDTO.builder()
                        .customerId(customer.getCustomerId())
                        .password(request.newPassword())
                        .build()
        );

        return ApiResponse.builder()
                .message("Password reset successfully")
                .build();
    }

    private String otpKey(OtpPurpose purpose, Long customerId) {
        return "otp:" + purpose.name() + ":" + customerId;
    }

    private void generateAndSendOtp(CustomerResponseDTO customer, OtpPurpose purpose) {
        String otp = otpGenerator.generateOtp();
        OtpData otpData = new OtpData();

        otpData.setOtp(otp);
        otpData.setAttempts(0);
        otpData.setGeneratedAt(LocalDateTime.now());

        redisTemplate.opsForValue().set(
                otpKey(purpose, customer.getCustomerId()),
                otpData,
                Duration.ofMinutes(5)
        );

        emailService.sendOtp(customer.getEmail(), otp);
    }

    /**
     * @param consume when true, deletes the OTP once validated so it can't
     *                be reused (login, final reset step); when false, only
     *                checks it so a later step can still consume it
     *                (the reset-flow's separate "verify OTP" pre-check).
     */
    private void validateOtp(CustomerResponseDTO customer, String otp, OtpPurpose purpose, boolean consume) {
        String key = otpKey(purpose, customer.getCustomerId());
        OtpData otpData = redisTemplate.opsForValue().get(key);

        if (otpData == null) {
            throw new CustomErrorException("OTP expired");
        }

        if (!otpData.getOtp().equals(otp)) {
            throw new CustomErrorException("Invalid OTP");
        }

        if (consume) {
            redisTemplate.delete(key);
        }
    }
}
