package com.bank.auth_service.service;

import com.bank.auth_service.dto.RegisterRequestDTO;
import com.bank.auth_service.dto.RegisterResponseDTO;

public interface ApplicationUserService {

    RegisterResponseDTO registerUser(RegisterRequestDTO request);
    RegisterResponseDTO updateUser(RegisterRequestDTO request);
}
