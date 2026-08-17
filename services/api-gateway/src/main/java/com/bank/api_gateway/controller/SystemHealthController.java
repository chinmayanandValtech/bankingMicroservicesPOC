package com.bank.api_gateway.controller;

import com.bank.api_gateway.dto.SystemHealthResponse;
import com.bank.api_gateway.service.SystemHealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemHealthController {

    private final SystemHealthService systemHealthService;

    public SystemHealthController(
            SystemHealthService systemHealthService
    ) {
        this.systemHealthService = systemHealthService;
    }

    @GetMapping("/health")
    public SystemHealthResponse getSystemHealth() {
        return systemHealthService.getSystemHealth();
    }
}