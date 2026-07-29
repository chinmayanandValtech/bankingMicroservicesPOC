package com.bank.auth_service.controllers;

import com.bank.auth_service.dto.RegisterRequestDTO;
import com.bank.auth_service.dto.RegisterResponseDTO;
import com.bank.auth_service.service.ApplicationUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("/api/auth/register")
    RegisterResponseDTO registerUser(@RequestBody RegisterRequestDTO request) {
        return applicationUserService.registerUser(request);
    }
}
