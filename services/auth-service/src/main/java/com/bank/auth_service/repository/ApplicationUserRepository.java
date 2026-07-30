package com.bank.auth_service.repository;

import com.bank.auth_service.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);
    Optional<ApplicationUser> findByCustomerId(Long customerId);
}
