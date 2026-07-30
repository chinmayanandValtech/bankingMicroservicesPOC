package com.bank.auth_service.service;

import com.bank.auth_service.config.LoginRequestDTO;
import com.bank.auth_service.config.LoginResponseDTO;

public interface AuthenticationService {
    LoginResponseDTO login(LoginRequestDTO request);
}
