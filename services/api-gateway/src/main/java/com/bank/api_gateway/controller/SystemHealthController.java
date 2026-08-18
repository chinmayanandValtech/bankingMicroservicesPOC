package com.bank.api_gateway.controller;

import com.bank.api_gateway.dto.SystemHealthResponse;
import com.bank.api_gateway.service.SystemHealthService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:5173",
                "https://banking-management-frontend.vercel.app"
        }
)
public class SystemHealthController {

    private final SystemHealthService systemHealthService;

    public SystemHealthController(
            SystemHealthService systemHealthService
    ) {
        this.systemHealthService = systemHealthService;
    }

    @GetMapping("/health")
    public Mono<SystemHealthResponse> getSystemHealth() {
        return systemHealthService.getSystemHealth();
    }
}