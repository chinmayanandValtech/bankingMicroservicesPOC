package com.bank.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final Map<String, String> otpStore = new HashMap<>();

    public String generateOtp() {

        Random random = new Random();

        return String.valueOf(
                100000 + random.nextInt(900000)
        );
    }

    public void saveOtp(String email, String otp) {

        otpStore.put(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {

        String savedOtp = otpStore.get(email);

        if (savedOtp != null && savedOtp.equals(otp)) {

            otpStore.remove(email);

            return true;
        }

        return false;
    }
}