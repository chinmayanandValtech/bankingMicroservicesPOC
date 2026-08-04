package com.bank.auth_service.service;

import org.springframework.stereotype.Service;

public interface EmailService {

    void sendOtp(String to, String otp);

}
