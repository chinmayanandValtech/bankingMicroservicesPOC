package com.bank.account_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${HOSTNAME:unknown}")
    private String hostname;
    @GetMapping("/pod")
    public String pod() {
        return hostname;
    }
}
