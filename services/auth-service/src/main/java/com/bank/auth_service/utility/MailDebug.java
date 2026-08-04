package com.bank.auth_service.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailDebug {

    @Value("${MAIL_USERNAME:NOT_FOUND}")
    private String username;

    @Value("${MAIL_PASSWORD:NOT_FOUND}")
    private String password;

    @PostConstruct
    public void init() {
        System.out.println("MAIL_USERNAME = " + username);
        System.out.println("MAIL_PASSWORD = " + password);
    }
}
