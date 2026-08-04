package com.bank.auth_service.service.impl;

import com.bank.auth_service.service.EmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String to, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Your One-Time Password (OTP)");

            helper.setText("""
                    Dear Customer,

                    Your One-Time Password (OTP) is:

                    %s

                    This OTP is valid for 5 minutes.

                    Please do not share this OTP with anyone.

                    Regards,
                    Banking Management System
                    """.formatted(otp));

            mailSender.send(message);

            log.info("OTP email sent successfully to {}", to);

        } catch (MessagingException | MailSendException ex) {

            log.error("Failed to send OTP email to {}", to, ex);

            throw new RuntimeException("Unable to send OTP email", ex);
        }
    }
}