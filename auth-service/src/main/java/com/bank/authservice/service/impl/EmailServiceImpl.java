package com.bank.authservice.service.impl;

import com.bank.authservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl extends EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("banking.microservice.demo@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Banking OTP Verification");

        message.setText(
                "Hello,\n\n" +
                        "Your OTP is: " + otp +
                        "\n\nValid for 5 minutes."
        );

        mailSender.send(message);
    }
}