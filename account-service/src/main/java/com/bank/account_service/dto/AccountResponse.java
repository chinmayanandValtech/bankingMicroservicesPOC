package com.bank.account_service.dto;

import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private Long customerId;

    private String customerName;

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus status;

    private LocalDateTime createdAt;
}
