package com.bank.financialintelligence.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/ai/health")
    public Map<String, String> health() {

        return Map.of(
                "service", "Financial Intelligence Service",
                "status", "UP"
        );

    }
}