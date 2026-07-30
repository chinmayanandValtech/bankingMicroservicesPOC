package com.bank.account_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponse {

    private String accountNumber;

    private BigDecimal balance;

    private LocalDateTime updatedAt;
}
