package com.bank.auth_service.utility;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class OtpGenerator {

    // Create a single, reusable instance of SecureRandom (Thread-safe)
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {
        int otpLength = 6;
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            // secureRandom.nextInt(10) generates a cryptographically secure integer between 0 and 9
            int digit = secureRandom.nextInt(10);
            otp.append(digit);
        }

        return otp.toString();
    }
}
